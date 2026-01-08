package com.example.birent.data.repository

import com.example.birent.data.local.OrderStatus
import com.example.birent.data.local.dao.OrderDao
import com.example.birent.data.local.entity.OrderEntity
import com.example.birent.data.local.entity.OrderItemEntity
import com.example.birent.data.mapper.toDomain
import com.example.birent.data.prefs.SessionManager
import com.example.birent.domain.model.Order
import com.example.birent.domain.model.PickupPoint
import com.example.birent.domain.repository.CartRepository
import com.example.birent.domain.repository.OrderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val cartRepository: CartRepository,
    private val sessionManager: SessionManager
) : OrderRepository {

    override suspend fun createOrder(pickupPointId: Long, totalPrice: Double): Result<Unit> {
        val userId = sessionManager.userId.first() ?: return Result.failure(Exception("User not logged in"))
        val cartItems = cartRepository.getCartItemsFlow().first()

        if (cartItems.isEmpty()) {
            return Result.failure(Exception("Cart is empty"))
        }

        val order = OrderEntity(
            userId = userId,
            pickupPointId = pickupPointId,
            status = OrderStatus.AWAITING_PICKUP,
            totalPrice = totalPrice,
            createdDate = LocalDateTime.now(),
            startTime = null,
            expectedEndTime = null
        )

        try {
            val orderId = orderDao.insertOrder(order)
            val orderItems = cartItems.map {
                val unitPrice = it.calculatedPrice / it.quantity
                OrderItemEntity(
                    orderId = orderId,
                    bikeId = it.bike.id,
                    quantity = it.quantity,
                    priceSnapshot = unitPrice,
                    rentType = it.rentType,
                    duration = it.duration
                )
            }
            orderDao.insertOrderItems(orderItems)
            cartRepository.clearCart()
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUserOrdersFlow(): Flow<List<Order>> {
        return sessionManager.userId.flatMapLatest { userId ->
            if (userId == null) {
                emptyFlow()
            } else {
                orderDao.getUserOrdersFlow(userId).map { list ->
                    list.map { it.toDomain() }
                }
            }
        }
    }

    override suspend fun getOrderById(orderId: Long): Order? {
        return orderDao.getOrderById(orderId)?.toDomain()
    }

    override suspend fun getPickupPoints(): List<PickupPoint> {
        return orderDao.getPickupPoints().map { it.toDomain() }
    }

    override suspend fun updateStatus(orderId: Long, status: OrderStatus) {
        orderDao.updateStatus(orderId, status)
    }

    override suspend fun startRent(orderId: Long, durationHours: Long) {
        val now = LocalDateTime.now()
        val expectedEnd = now.plusHours(durationHours)
        orderDao.startRent(orderId, OrderStatus.IN_RENT, now, expectedEnd)
    }

    override suspend fun completeOrder(orderId: Long, penalty: Double) {
        orderDao.completeOrder(orderId, OrderStatus.COMPLETED, penalty)
    }

    override suspend fun checkOverdueOrders() {
        val activeOrders = orderDao.getActiveOrders()
        val now = LocalDateTime.now()

        activeOrders.forEach { order ->
            if (order.expectedEndTime != null && now.isAfter(order.expectedEndTime)) {
                if (order.status == OrderStatus.IN_RENT) {
                    orderDao.updateStatus(order.id, OrderStatus.OVERDUE)
                }
            }
        }
    }
}