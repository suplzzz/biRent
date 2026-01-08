package com.example.birent.data.repository

import com.example.birent.data.local.dao.CartDao
import com.example.birent.data.local.dao.UserDao
import com.example.birent.data.local.entity.UserEntity
import com.example.birent.data.mapper.toDomain
import com.example.birent.data.prefs.SessionManager
import com.example.birent.domain.model.User
import com.example.birent.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import java.security.MessageDigest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val cartDao: CartDao,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun register(phone: String, fullName: String, password: String): Result<Unit> {
        return try {
            val existing = userDao.getUserByPhone(phone)
            if (existing != null) return Result.failure(Exception("Телефон уже зарегистрирован"))

            val hash = hashPassword(password)
            val userId = userDao.insertUser(UserEntity(phone = phone, fullName = fullName, passwordHash = hash))

            // Слияние корзины гостя с новым юзером (Req 2.11, 2.1)
            cartDao.bindGuestCartToUser(userId)

            sessionManager.saveUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(phone: String, password: String): Result<User> {
        val user = userDao.getUserByPhone(phone) ?: return Result.failure(Exception("Пользователь не найден"))
        if (user.passwordHash != hashPassword(password)) return Result.failure(Exception("Неверный пароль"))

        cartDao.bindGuestCartToUser(user.id)
        sessionManager.saveUser(user.id)

        return Result.success(user.toDomain())
    }

    override suspend fun getCurrentUser(): User? {
        val id = sessionManager.userId.first() ?: return null
        return userDao.getUserById(id)?.toDomain()
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override suspend fun getGuestId(): Long? = sessionManager.userId.first()

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(bytes).fold("") { str, it -> str + "%02x".format(it) }
    }
}