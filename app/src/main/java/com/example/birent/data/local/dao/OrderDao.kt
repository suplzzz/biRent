package com.example.birent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.birent.data.local.OrderStatus
import com.example.birent.data.local.entity.OrderEntity
import com.example.birent.data.local.entity.OrderItemEntity
import com.example.birent.data.local.entity.PickupPointEntity
import com.example.birent.data.local.model.OrderWithItems
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Transaction
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdDate DESC")
    fun getUserOrdersFlow(userId: Long): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Long): OrderWithItems?

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateStatus(orderId: Long, status: OrderStatus)

    @Query("UPDATE orders SET status = :status, startTime = :startTime, expectedEndTime = :expectedEndTime WHERE id = :orderId")
    suspend fun startRent(orderId: Long, status: OrderStatus, startTime: LocalDateTime, expectedEndTime: LocalDateTime)

    @Query("UPDATE orders SET status = :status, penalty = :penalty WHERE id = :orderId")
    suspend fun completeOrder(orderId: Long, status: OrderStatus, penalty: Double)

    @Query("SELECT * FROM orders WHERE status = 'IN_RENT'")
    suspend fun getActiveOrders(): List<OrderEntity>

    @Query("SELECT * FROM pickup_points")
    suspend fun getPickupPoints(): List<PickupPointEntity>

    @Insert
    suspend fun insertPickupPoints(points: List<PickupPointEntity>)
}