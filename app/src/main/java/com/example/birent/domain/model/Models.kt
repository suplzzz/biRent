package com.example.birent.domain.model

import com.example.birent.data.local.BikeType
import com.example.birent.data.local.OrderStatus
import com.example.birent.data.local.RentType
import java.time.LocalDateTime

data class User(
    val id: Long,
    val phone: String,
    val fullName: String
)

data class Bike(
    val id: Long,
    val model: String,
    val type: BikeType,
    val color: String,
    val frameSize: String,
    val speeds: Int,
    val priceHour: Double,
    val priceDay: Double,
    val imageUrl: String,
    val isAvailable: Boolean = true
)

data class CartItem(
    val id: Long,
    val bike: Bike,
    val quantity: Int,
    val rentType: RentType,
    val duration: Int,
    val calculatedPrice: Double,
    val discountPercent: Int
)

data class PickupPoint(
    val id: Long,
    val name: String,
    val address: String
)

data class Order(
    val id: Long,
    val status: OrderStatus,
    val totalPrice: Double,
    val createdDate: LocalDateTime,
    val startTime: LocalDateTime?,
    val expectedEndTime: LocalDateTime?,
    val penalty: Double,
    val pickupPointName: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val bikeModel: String,
    val quantity: Int,
    val priceSnapshot: Double,
    val rentType: RentType,
    val duration: Int
)