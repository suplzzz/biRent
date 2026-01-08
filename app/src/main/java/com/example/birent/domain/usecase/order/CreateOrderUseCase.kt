package com.example.birent.domain.usecase.order

import com.example.birent.domain.repository.AuthRepository
import com.example.birent.domain.repository.CartRepository
import com.example.birent.domain.repository.OrderRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(pickupPointId: Long): Result<Unit> {
        val user = authRepository.getCurrentUser()
        if (user == null) {
            return Result.failure(Exception("Необходима авторизация"))
        }

        val cartItems = cartRepository.getCartItemsFlow().first()

        if (cartItems.isEmpty()) {
            return Result.failure(Exception("Корзина пуста"))
        }

        val totalPrice = cartItems.sumOf { it.calculatedPrice }

        return orderRepository.createOrder(pickupPointId, totalPrice)
    }
}