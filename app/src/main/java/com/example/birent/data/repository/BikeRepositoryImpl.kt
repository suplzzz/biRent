package com.example.birent.data.repository

import com.example.birent.data.local.BikeType
import com.example.birent.data.local.dao.BikeDao
import com.example.birent.data.mapper.toDomain
import com.example.birent.domain.model.Bike
import com.example.birent.domain.repository.BikeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BikeRepositoryImpl @Inject constructor(
    private val bikeDao: BikeDao
) : BikeRepository {

    override suspend fun getBikeById(id: Long): Bike? {
        val entity = bikeDao.getBikeById(id) ?: return null
        val rented = bikeDao.getRentedCountSync(id) ?: 0
        return entity.toDomain(rented)
    }

    override fun searchBikes(
        query: String?,
        type: BikeType?,
        minSpeeds: Int,
        maxSpeeds: Int,
        minPrice: Double?,
        maxPrice: Double?
    ): Flow<List<Bike>> {
        return bikeDao.searchBikesFlow(query, type, minPrice, maxPrice).map { entities ->
            entities.map { entity ->
                val rented = bikeDao.getRentedCountSync(entity.id) ?: 0
                entity.toDomain(rented)
            }
        }
    }

    override suspend fun getAvailabilityInfo(bikeId: Long): Pair<Int, Int> {
        val bike = bikeDao.getBikeById(bikeId) ?: return Pair(0, 0)
        val rented = bikeDao.getRentedCountSync(bikeId) ?: 0
        return Pair(bike.totalQuantity, rented)
    }
}