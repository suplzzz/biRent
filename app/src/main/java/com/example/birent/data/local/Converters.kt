package com.example.birent.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun fromBikeType(value: BikeType): String = value.name

    @TypeConverter
    fun toBikeType(value: String): BikeType = BikeType.valueOf(value)

    @TypeConverter
    fun fromRentType(value: RentType): String = value.name

    @TypeConverter
    fun toRentType(value: String): RentType = RentType.valueOf(value)

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String = value.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = OrderStatus.valueOf(value)
}

enum class BikeType {
    MOUNTAIN, CITY, ROAD, ELECTRIC, KIDS
}

enum class RentType {
    HOURLY, DAILY
}

enum class OrderStatus {
    AWAITING_PICKUP, // Ожидает выдачи
    IN_RENT,         // В аренде
    OVERDUE,         // Задержка возврата
    COMPLETED,       // Завершен
    CANCELED         // Отменен
}