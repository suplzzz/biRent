package com.example.birent.presentation.screen.auth.login

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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun processCommand(command: LoginCommand) {
        when (command) {
            is LoginCommand.InputPhone -> _state.update { it.copy(phone = command.phone, error = null) }
            is LoginCommand.InputPassword -> _state.update { it.copy(password = command.password, error = null) }
            LoginCommand.LoginClick -> login()
        }
    }

    private fun login() {
        val s = state.value
        if (s.phone.isBlank() || s.password.isBlank()) {
            _state.update { it.copy(error = "Заполните все поля") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = authRepository.login(s.phone, s.password)
            if (result.isSuccess) {
                _effect.send(LoginEffect.LoginSuccess)
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}

data class LoginState(
    val phone: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface LoginCommand {
    data class InputPhone(val phone: String) : LoginCommand
    data class InputPassword(val password: String) : LoginCommand
    data object LoginClick : LoginCommand
}

sealed interface LoginEffect {
    data object LoginSuccess : LoginEffect
}