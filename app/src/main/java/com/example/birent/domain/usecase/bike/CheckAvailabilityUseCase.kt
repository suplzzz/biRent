package com.example.birent.domain.usecase.bike

import com.example.birent.domain.repository.BikeRepository
import javax.inject.Inject

class CheckAvailabilityUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    // Реализация требования 2.12 (Резерв)
    suspend operator fun invoke(bikeId: Long): Boolean {
        val (total, rented) = bikeRepository.getAvailabilityInfo(bikeId)

        // На каждые 5 велосипедов в аренде - 1 резервный
        val reserve = rented / 5

        val available = total - rented - reserve
        return available > 0
    }
}