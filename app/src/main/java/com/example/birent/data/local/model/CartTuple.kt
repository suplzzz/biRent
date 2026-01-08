package com.example.birent.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.birent.data.local.entity.BikeEntity
import com.example.birent.data.local.entity.CartItemEntity

data class CartTuple(
    @Embedded val cartItem: CartItemEntity,

    @Relation(parentColumn = "bikeId", entityColumn = "id")
    val bike: BikeEntity
)