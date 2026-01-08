package com.example.birent.domain.usecase.order

import com.example.birent.data.local.OrderStatus
import com.example.birent.data.local.RentType
import com.example.birent.domain.repository.OrderRepository
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

class AdminOrderActionUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend fun startRent(orderId: Long) {
        val order = orderRepository.getOrderById(orderId) ?: return

        val maxDurationHours = order.items.maxOfOrNull { item ->
            when (item.rentType) {
                RentType.HOURLY -> item.duration.toLong()
                RentType.DAILY -> item.duration.toLong() * 24
            }
        } ?: 1L

        orderRepository.startRent(orderId, maxDurationHours)
    }

    suspend fun completeRent(orderId: Long) {
        val order = orderRepository.getOrderById(orderId) ?: return
        var penalty = 0.0

        if (order.expectedEndTime != null) {
            val now = LocalDateTime.now()
            if (now.isAfter(order.expectedEndTime)) {
                val overdueMinutes = Duration.between(order.expectedEndTime, now).toMinutes()
                val overdueHours = kotlin.math.ceil(overdueMinutes / 60.0).toLong().coerceAtLeast(1)

                val hourlyRateBase = order.items.sumOf { item ->
                    val unitPrice = item.priceSnapshot
                    val itemHourlyRate = if (item.rentType == RentType.DAILY) {
                        unitPrice / 24.0
                    } else {
                        unitPrice
                    }
                    itemHourlyRate * item.quantity
                }

                penalty = hourlyRateBase * 1.25 * overdueHours
            }
        }

        penalty = kotlin.math.round(penalty * 100) / 100.0

        orderRepository.completeOrder(orderId, penalty)
    }

    suspend fun cancelOrder(orderId: Long) {
        orderRepository.updateStatus(orderId, OrderStatus.CANCELED)
    }
}