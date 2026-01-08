package com.example.birent.domain.usecase.order

import com.example.birent.data.local.OrderStatus
import com.example.birent.data.local.RentType
import com.example.birent.domain.repository.OrderRepository
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

    suspend fun completeRent(orderId: Long, penalty: Double) {
        orderRepository.completeOrder(orderId, penalty)
    }

    suspend fun cancelOrder(orderId: Long) {
        orderRepository.updateStatus(orderId, OrderStatus.CANCELED)
    }
}