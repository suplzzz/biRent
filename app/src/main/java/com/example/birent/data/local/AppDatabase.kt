package com.example.birent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.birent.data.local.dao.BikeDao
import com.example.birent.data.local.dao.CartDao
import com.example.birent.data.local.dao.OrderDao
import com.example.birent.data.local.dao.UserDao
import com.example.birent.data.local.entity.BikeEntity
import com.example.birent.data.local.entity.CartItemEntity
import com.example.birent.data.local.entity.OrderEntity
import com.example.birent.data.local.entity.OrderItemEntity
import com.example.birent.data.local.entity.PickupPointEntity
import com.example.birent.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        BikeEntity::class,
        PickupPointEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bikeDao(): BikeDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
}