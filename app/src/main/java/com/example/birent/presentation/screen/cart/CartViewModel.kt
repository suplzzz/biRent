package com.example.birent.presentation.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birent.data.local.RentType
import com.example.birent.domain.model.CartItem
import com.example.birent.domain.repository.AuthRepository
import com.example.birent.domain.usecase.cart.ManageCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val manageCartUseCase: ManageCartUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    val state: StateFlow<CartState> = manageCartUseCase.getCartFlow()
        .map { items ->
            val total = items.sumOf { it.calculatedPrice }
            CartState(items = items, totalPrice = total)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartState())

    private val _effect = Channel<CartEffect>()
    val effect = _effect.receiveAsFlow()

    fun processCommand(command: CartCommand) {
        when (command) {
            is CartCommand.UpdateItem -> updateItem(command.id, command.quantity, command.duration, command.rentType)
            is CartCommand.RemoveItem -> removeItem(command.id)
            CartCommand.ClearCart -> clearCart()
            CartCommand.Checkout -> checkout()
        }
    }

    private fun updateItem(id: Long, quantity: Int, duration: Int, rentType: RentType) {
        viewModelScope.launch {
            if (quantity > 0 && duration > 0) {
                manageCartUseCase.updateItem(id, quantity, duration, rentType)
            }
        }
    }

    private fun removeItem(id: Long) {
        viewModelScope.launch { manageCartUseCase.removeItem(id) }
    }

    private fun clearCart() {
        viewModelScope.launch { manageCartUseCase.clearCart() }
    }

    private fun checkout() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                _effect.send(CartEffect.NavigateToCreateOrder)
            } else {
                _effect.send(CartEffect.NavigateToLogin)
            }
        }
    }
}

data class CartState(
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0
)

sealed interface CartCommand {
    data class UpdateItem(val id: Long, val quantity: Int, val duration: Int, val rentType: RentType) : CartCommand
    data class RemoveItem(val id: Long) : CartCommand
    data object ClearCart : CartCommand
    data object Checkout : CartCommand
}

sealed interface CartEffect {
    data object NavigateToCreateOrder : CartEffect
    data object NavigateToLogin : CartEffect
}