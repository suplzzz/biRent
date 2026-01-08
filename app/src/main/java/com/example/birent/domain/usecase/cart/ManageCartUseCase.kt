package com.example.birent.domain.usecase.cart

import com.example.birent.data.local.RentType
import com.example.birent.domain.model.CartItem
import com.example.birent.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    fun getCartFlow(): Flow<List<CartItem>> {
        return cartRepository.getCartItemsFlow()
    }

    suspend fun getCart(): List<CartItem> {
        return emptyList()
    }

    suspend fun addToCart(bikeId: Long, quantity: Int, rentType: RentType, duration: Int) {
        cartRepository.addToCart(bikeId, quantity, rentType, duration)
    }

    suspend fun updateItem(cartId: Long, quantity: Int, duration: Int, rentType: RentType) {
        cartRepository.updateCartItem(cartId, quantity, duration, rentType)
    }

    suspend fun removeItem(cartId: Long) {
        cartRepository.removeFromCart(cartId)
    }

    suspend fun clearCart() {
        cartRepository.clearCart()
    }
}