package com.example.birent.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.birent.data.local.BikeType
import com.example.birent.data.local.entity.BikeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BikeDao {
    @Transaction
    @Query("""
        SELECT * FROM bikes 
        WHERE (:query IS NULL OR model LIKE '%' || :query || '%')
        AND (:type IS NULL OR type = :type)
        AND (:minPrice IS NULL OR priceHour >= :minPrice)
        AND (:maxPrice IS NULL OR priceHour <= :maxPrice)
    """)
    fun searchBikesFlow(
        query: String?,
        type: BikeType?,
        minPrice: Double?,
        maxPrice: Double?
    ): Flow<List<BikeEntity>>

    @Query("SELECT * FROM bikes WHERE id = :id LIMIT 1")
    suspend fun getBikeById(id: Long): BikeEntity?

    @Query("""
        SELECT SUM(oi.quantity) 
        FROM order_items oi
        JOIN orders o ON oi.orderId = o.id
        WHERE oi.bikeId = :bikeId 
        AND (o.status = 'AWAITING_PICKUP' OR o.status = 'IN_RENT' OR o.status = 'OVERDUE')
    """)
    suspend fun getRentedCountSync(bikeId: Long): Int?

    @androidx.room.Insert
    suspend fun insertBikes(bikes: List<BikeEntity>)
}