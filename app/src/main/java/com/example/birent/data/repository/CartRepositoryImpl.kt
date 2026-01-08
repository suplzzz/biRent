package com.example.birent.data.repository

import com.example.birent.data.local.RentType
import com.example.birent.data.local.dao.CartDao
import com.example.birent.data.local.entity.CartItemEntity
import com.example.birent.data.mapper.toDomain
import com.example.birent.data.prefs.SessionManager
import com.example.birent.domain.model.CartItem
import com.example.birent.domain.repository.CartRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val sessionManager: SessionManager
) : CartRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCartItemsFlow(): Flow<List<CartItem>> {
        return sessionManager.userId.flatMapLatest { userId ->
            cartDao.getCartItemsFlow(userId).map { list ->
                list.map { it.toDomain() }
            }
        }
    }

    override suspend fun addToCart(bikeId: Long, quantity: Int, rentType: RentType, duration: Int) {
        val userId = sessionManager.userId.first()
        val item = CartItemEntity(
            userId = userId,
            bikeId = bikeId,
            quantity = quantity,
            rentType = rentType,
            duration = duration
        )
        cartDao.addToCart(item)
    }

    override suspend fun updateCartItem(cartId: Long, quantity: Int, duration: Int, rentType: RentType) {
        cartDao.updateCartItem(cartId, quantity, duration, rentType)
    }

    override suspend fun removeFromCart(cartId: Long) {
        cartDao.removeFromCart(cartId)
    }

    override suspend fun clearCart() {
        val userId = sessionManager.userId.first()
        cartDao.clearCart(userId)
    }

    override suspend fun bindGuestCartToUser(userId: Long) {
        cartDao.bindGuestCartToUser(userId)
    }
}