package com.example.birent.data.mapper

import com.example.birent.data.local.RentType
import com.example.birent.data.local.entity.BikeEntity
import com.example.birent.data.local.entity.CartItemEntity
import com.example.birent.data.local.entity.OrderEntity
import com.example.birent.data.local.entity.PickupPointEntity
import com.example.birent.data.local.entity.UserEntity
import com.example.birent.data.local.model.CartTuple
import com.example.birent.data.local.model.OrderWithItems
import com.example.birent.domain.model.Bike
import com.example.birent.domain.model.CartItem
import com.example.birent.domain.model.Order
import com.example.birent.domain.model.OrderItem
import com.example.birent.domain.model.PickupPoint
import com.example.birent.domain.model.User

fun UserEntity.toDomain() = User(
    id = id,
    phone = phone,
    fullName = fullName
)

fun BikeEntity.toDomain(rentedCount: Int = 0): Bike {
    val reserve = rentedCount / 5
    val available = totalQuantity - rentedCount - reserve

    return Bike(
        id = id,
        model = model,
        type = type,
        color = color,
        frameSize = frameSize,
        speeds = speeds,
        priceHour = priceHour,
        priceDay = priceDay,
        imageUrl = imageUrl,
        isAvailable = available > 0
    )
}

fun PickupPointEntity.toDomain() = PickupPoint(id, name, address)

fun CartTuple.toDomain(): CartItem {
    val duration = cartItem.duration
    val (finalPrice, discount) = calculatePrice(
        priceHour = bike.priceHour,
        priceDay = bike.priceDay,
        type = cartItem.rentType,
        duration = duration
    )

    return CartItem(
        id = cartItem.id,
        bike = bike.toDomain(),
        quantity = cartItem.quantity,
        rentType = cartItem.rentType,
        duration = duration,
        calculatedPrice = finalPrice * cartItem.quantity,
        discountPercent = discount
    )
}

fun OrderWithItems.toDomain() = Order(
    id = order.id,
    status = order.status,
    totalPrice = order.totalPrice,
    createdDate = order.createdDate,
    startTime = order.startTime,
    expectedEndTime = order.expectedEndTime,
    penalty = order.penalty,
    pickupPointName = pickupPoint?.name ?: "Неизвестно",
    items = items.map {
        OrderItem(
            bikeModel = "Велосипед #${it.bikeId}",
            quantity = it.quantity,
            priceSnapshot = it.priceSnapshot,
            rentType = it.rentType,
            duration = it.duration
        )
    }
)

private fun calculatePrice(priceHour: Double, priceDay: Double, type: RentType, duration: Int): Pair<Double, Int> {
    return when (type) {
        RentType.HOURLY -> {
            val base = priceHour * duration
            if (duration >= 3) Pair(base * 0.9, 10) else Pair(base, 0)
        }
        RentType.DAILY -> {
            val base = priceDay * duration
            when {
                duration >= 7 -> Pair(base * 0.75, 25)
                duration >= 3 -> Pair(base * 0.9, 10)
                else -> Pair(base, 0)
            }
        }
    }
}