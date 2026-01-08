package com.example.birent.presentation.screen.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            if (it is LoginEffect.LoginSuccess) {
                onLoginSuccess()
            }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Вход в BiRent", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.phone,
                onValueChange = { viewModel.processCommand(LoginCommand.InputPhone(it)) },
                label = { Text("Номер телефона") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                onValueChange = { viewModel.processCommand(LoginCommand.InputPassword(it)) },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.error != null) {
                Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.processCommand(LoginCommand.LoginClick) }
                ) {
                    Text("Войти")
                }

                TextButton(onClick = onNavigateToRegister) {
                    Text("Нет аккаунта? Зарегистрироваться")
                }
            }
        }
    }
}