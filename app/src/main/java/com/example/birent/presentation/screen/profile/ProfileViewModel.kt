package com.example.birent.presentation.screen.profile

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birent.data.local.OrderStatus
import com.example.birent.data.prefs.SessionManager
import com.example.birent.domain.model.Order
import com.example.birent.domain.model.User
import com.example.birent.domain.repository.AuthRepository
import com.example.birent.domain.repository.OrderRepository
import com.example.birent.domain.usecase.order.AdminOrderActionUseCase
import com.example.birent.domain.usecase.order.ProcessOverdueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    private val adminOrderActionUseCase: AdminOrderActionUseCase,
    private val processOverdueUseCase: ProcessOverdueUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        sessionManager.userId
            .onEach { userId ->
                if (userId != null) {
                    val user = authRepository.getCurrentUser()
                    _state.update { it.copy(user = user) }
                } else {
                    _state.update { ProfileState() }
                }
            }
            .launchIn(viewModelScope)

        orderRepository.getUserOrdersFlow()
            .onEach { orders ->
                processOverdueUseCase()
                _state.update { it.copy(orders = orders) }
            }
            .launchIn(viewModelScope)

        startTimerLoop()
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun adminStartRent(orderId: Long) {
        viewModelScope.launch {
            adminOrderActionUseCase.startRent(orderId)
        }
    }

    fun adminCompleteRent(orderId: Long) {
        viewModelScope.launch {
            adminOrderActionUseCase.completeRent(orderId, 0.0)
        }
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            adminOrderActionUseCase.cancelOrder(orderId)
        }
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun startTimerLoop() {
        viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()
                _state.update { s ->
                    val updatedTimers = s.orders
                        .filter { it.status == OrderStatus.IN_RENT && it.expectedEndTime != null }
                        .associate { order ->
                            val remaining = Duration.between(now, order.expectedEndTime)
                            val text = if (remaining.isNegative) "ПРОСРОЧЕНО"
                            else String.format("%02d:%02d:%02d", remaining.toHours(), remaining.toMinutesPart(), remaining.toSecondsPart())
                            order.id to text
                        }
                    s.copy(timers = updatedTimers)
                }
                delay(1000)
            }
        }
    }
}

data class ProfileState(
    val user: User? = null,
    val orders: List<Order> = emptyList(),
    val timers: Map<Long, String> = emptyMap()
)