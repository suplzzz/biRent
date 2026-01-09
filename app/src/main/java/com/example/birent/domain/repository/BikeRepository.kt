package com.example.birent.domain.repository

import com.example.birent.data.local.BikeType
import com.example.birent.domain.model.Bike
import kotlinx.coroutines.flow.Flow

interface BikeRepository {
    suspend fun getBikeById(id: Long): Bike?

    fun searchBikes(
        query: String? = null,
        type: BikeType? = null,
        frameSize: String? = null,
        minSpeeds: Int = 0,
        maxSpeeds: Int = 100,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): Flow<List<Bike>>

    suspend fun getAvailabilityInfo(bikeId: Long): Pair<Int, Int>
}