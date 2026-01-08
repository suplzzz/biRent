package com.example.birent.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.birent.data.local.BikeType
import com.example.birent.data.local.OrderStatus
import com.example.birent.data.local.RentType
import java.time.LocalDateTime

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phone: String,
    val fullName: String,
    val passwordHash: String
)

@Entity(tableName = "bikes")
data class BikeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val model: String,
    val type: BikeType,
    val color: String,
    val frameSize: String,
    val speeds: Int,
    val priceHour: Double,
    val priceDay: Double,
    val totalQuantity: Int,
    val description: String,
    val imageUrl: String
)

@Entity(tableName = "pickup_points")
data class PickupPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val address: String
)

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = BikeEntity::class, parentColumns = ["id"], childColumns = ["bikeId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long?,
    val bikeId: Long,
    val quantity: Int,
    val rentType: RentType,
    val duration: Int
)

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = PickupPointEntity::class, parentColumns = ["id"], childColumns = ["pickupPointId"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val pickupPointId: Long?,
    val status: OrderStatus,
    val totalPrice: Double,
    val createdDate: LocalDateTime,
    val startTime: LocalDateTime?,
    val expectedEndTime: LocalDateTime?,
    val penalty: Double = 0.0
)

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(entity = OrderEntity::class, parentColumns = ["id"], childColumns = ["orderId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = BikeEntity::class, parentColumns = ["id"], childColumns = ["bikeId"], onDelete = ForeignKey.NO_ACTION)
    ]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val bikeId: Long,
    val quantity: Int,
    val priceSnapshot: Double,
    val rentType: RentType,
    val duration: Int
)