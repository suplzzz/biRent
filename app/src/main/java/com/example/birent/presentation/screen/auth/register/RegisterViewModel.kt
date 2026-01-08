package com.example.birent.presentation.screen.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birent.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _effect = Channel<RegisterEffect>()
    val effect = _effect.receiveAsFlow()

    fun processCommand(command: RegisterCommand) {
        when (command) {
            is RegisterCommand.InputName -> _state.update { it.copy(fullName = command.value, error = null) }
            is RegisterCommand.InputPhone -> _state.update { it.copy(phone = command.value, error = null) }
            is RegisterCommand.InputPassword -> _state.update { it.copy(password = command.value, error = null) }
            is RegisterCommand.InputConfirm -> _state.update { it.copy(confirm = command.value, error = null) }
            RegisterCommand.RegisterClick -> register()
        }
    }

    private fun register() {
        val s = state.value
        if (s.fullName.isBlank() || s.phone.isBlank() || s.password.isBlank()) {
            _state.update { it.copy(error = "Заполните все обязательные поля") }
            return
        }
        if (s.password != s.confirm) {
            _state.update { it.copy(error = "Пароли не совпадают") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = authRepository.register(s.phone, s.fullName, s.password)
            if (result.isSuccess) {
                _effect.send(RegisterEffect.RegisterSuccess)
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}

data class RegisterState(
    val fullName: String = "",
    val phone: String = "",
    val password: String = "",
    val confirm: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface RegisterCommand {
    data class InputName(val value: String) : RegisterCommand
    data class InputPhone(val value: String) : RegisterCommand
    data class InputPassword(val value: String) : RegisterCommand
    data class InputConfirm(val value: String) : RegisterCommand
    data object RegisterClick : RegisterCommand
}

sealed interface RegisterEffect {
    data object RegisterSuccess : RegisterEffect
}