package com.example.birent.domain.repository

import com.example.birent.data.local.RentType
import com.example.birent.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItemsFlow(): Flow<List<CartItem>>
    suspend fun addToCart(bikeId: Long, quantity: Int, rentType: RentType, duration: Int)
    suspend fun updateCartItem(cartId: Long, quantity: Int, duration: Int, rentType: RentType)
    suspend fun removeFromCart(cartId: Long)
    suspend fun clearCart()
    suspend fun bindGuestCartToUser(userId: Long)
}