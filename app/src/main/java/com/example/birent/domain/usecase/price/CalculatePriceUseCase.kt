package com.example.birent.domain.usecase.price

import com.example.birent.data.local.RentType
import javax.inject.Inject

class CalculatePriceUseCase @Inject constructor() {

    // Возвращает Pair(Итоговая цена, Процент скидки)
    operator fun invoke(priceHour: Double, priceDay: Double, type: RentType, duration: Int): Pair<Double, Int> {
        return when (type) {
            RentType.HOURLY -> {
                val basePrice = priceHour * duration
                if (duration >= 3) {
                    Pair(basePrice * 0.9, 10) // Скидка 10% от 3 часов
                } else {
                    Pair(basePrice, 0)
                }
            }
            RentType.DAILY -> {
                val basePrice = priceDay * duration
                when {
                    duration >= 7 -> Pair(basePrice * 0.75, 25) // Скидка 25% от 7 дней
                    duration >= 3 -> Pair(basePrice * 0.9, 10)  // Скидка 10% от 3 дней
                    else -> Pair(basePrice, 0)
                }
            }
        }
    }
}