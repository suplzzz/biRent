package com.example.birent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birent.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // В этом приложении стартовая точка всегда Каталог,
    // так как есть гостевой доступ.
    // Но нам нужно знать статус для отображения меню.
    val isUserLoggedIn: StateFlow<Boolean> = flow {
        emit(authRepository.getCurrentUser() != null)
    }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)
}