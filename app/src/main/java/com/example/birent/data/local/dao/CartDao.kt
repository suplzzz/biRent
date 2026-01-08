package com.example.birent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.birent.data.local.RentType
import com.example.birent.data.local.entity.CartItemEntity
import com.example.birent.data.local.model.CartTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Transaction
    @Query("SELECT * FROM cart_items WHERE userId = :userId OR (userId IS NULL AND :userId IS NULL)")
    fun getCartItemsFlow(userId: Long?): Flow<List<CartTuple>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity, duration = :duration, rentType = :rentType WHERE id = :cartId")
    suspend fun updateCartItem(cartId: Long, quantity: Int, duration: Int, rentType: RentType)

    @Query("DELETE FROM cart_items WHERE id = :cartId")
    suspend fun removeFromCart(cartId: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId OR (userId IS NULL AND :userId IS NULL)")
    suspend fun clearCart(userId: Long?)

    @Query("UPDATE cart_items SET userId = :userId WHERE userId IS NULL")
    suspend fun bindGuestCartToUser(userId: Long)
}