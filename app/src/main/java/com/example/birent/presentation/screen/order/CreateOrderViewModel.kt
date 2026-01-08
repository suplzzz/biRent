package com.example.birent.presentation.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birent.domain.model.PickupPoint
import com.example.birent.domain.repository.OrderRepository
import com.example.birent.domain.usecase.order.CreateOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateOrderViewModel @Inject constructor(
    private val createOrderUseCase: CreateOrderUseCase,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateOrderState())
    val state = _state.asStateFlow()

    private val _effect = Channel<CreateOrderEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadPickupPoints()
    }

    fun selectPoint(id: Long) {
        _state.update { it.copy(selectedPointId = id) }
    }

    fun submitOrder() {
        val pointId = state.value.selectedPointId ?: return
        viewModelScope.launch {
            val result = createOrderUseCase(pointId)
            if (result.isSuccess) {
                _effect.send(CreateOrderEffect.OrderCreated)
            } else {
                _effect.send(CreateOrderEffect.Error(result.exceptionOrNull()?.message ?: "Ошибка"))
            }
        }
    }

    private fun loadPickupPoints() {
        viewModelScope.launch {
            val points = orderRepository.getPickupPoints()
            _state.update { it.copy(pickupPoints = points) }
        }
    }
}

data class CreateOrderState(
    val pickupPoints: List<PickupPoint> = emptyList(),
    val selectedPointId: Long? = null
)

sealed interface CreateOrderEffect {
    data object OrderCreated : CreateOrderEffect
    data class Error(val msg: String) : CreateOrderEffect
}