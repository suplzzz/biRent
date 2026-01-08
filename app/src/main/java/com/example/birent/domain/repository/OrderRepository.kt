package com.example.birent.domain.repository

import com.example.birent.data.local.OrderStatus
import com.example.birent.domain.model.Order
import com.example.birent.domain.model.PickupPoint
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun createOrder(pickupPointId: Long, totalPrice: Double): Result<Unit>

    // ИСПРАВЛЕНО: Возвращаем Flow
    fun getUserOrdersFlow(): Flow<List<Order>>

    suspend fun getOrderById(orderId: Long): Order?
    suspend fun getPickupPoints(): List<PickupPoint>

    suspend fun updateStatus(orderId: Long, status: OrderStatus)
    suspend fun startRent(orderId: Long, durationHours: Long)
    suspend fun completeOrder(orderId: Long, penalty: Double)
    suspend fun checkOverdueOrders()
}