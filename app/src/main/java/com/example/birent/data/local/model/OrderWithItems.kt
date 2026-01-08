package com.example.birent.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.birent.data.local.entity.OrderEntity
import com.example.birent.data.local.entity.OrderItemEntity
import com.example.birent.data.local.entity.PickupPointEntity

data class OrderWithItems(
    @Embedded val order: OrderEntity,

    @Relation(parentColumn = "id", entityColumn = "orderId")
    val items: List<OrderItemEntity>,

    @Relation(parentColumn = "pickupPointId", entityColumn = "id")
    val pickupPoint: PickupPointEntity?
)