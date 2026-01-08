package com.example.birent.domain.repository

import com.example.birent.domain.model.User

interface AuthRepository {
    suspend fun register(phone: String, fullName: String, password: String): Result<Unit>
    suspend fun login(phone: String, password: String): Result<User>
    suspend fun getCurrentUser(): User?
    suspend fun logout()
    suspend fun getGuestId(): Long? // Для работы корзины без регистрации
}