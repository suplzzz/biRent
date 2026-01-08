package com.example.birent.domain.usecase.order

import com.example.birent.domain.repository.OrderRepository
import javax.inject.Inject

class ProcessOverdueUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    // Вызывается периодически или при входе в список заказов
    suspend operator fun invoke() {
        orderRepository.checkOverdueOrders()
    }
}