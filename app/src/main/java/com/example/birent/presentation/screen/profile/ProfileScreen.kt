package com.example.birent.presentation.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.birent.data.local.OrderStatus
import com.example.birent.domain.model.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Если юзера нет, кидаем на логин (или показываем заглушку)
    LaunchedEffect(state.user) {
        if (state.user == null) {
            // Можно тут редиректить, но лучше отобразить кнопку "Войти"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                actions = {
                    if (state.user != null) {
                        IconButton(onClick = {
                            viewModel.logout()
                            onNavigateToLogin()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.user == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Button(onClick = onNavigateToLogin) { Text("Войти или зарегистрироваться") }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Привет, ${state.user!!.fullName}!", style = MaterialTheme.typography.headlineSmall)
                    Text("Твои заказы:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                }
                items(state.orders) { order ->
                    OrderCard(
                        order = order,
                        timerText = state.timers[order.id],
                        onCancel = { viewModel.cancelOrder(order.id) },
                        onAdminStart = { viewModel.adminStartRent(order.id) },
                        onAdminComplete = { viewModel.adminCompleteRent(order.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    timerText: String?,
    onCancel: () -> Unit,
    onAdminStart: () -> Unit,
    onAdminComplete: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Заказ #${order.id}", fontWeight = FontWeight.Bold)
                StatusChip(order.status)
            }
            Spacer(Modifier.height(8.dp))
            Text("Пункт: ${order.pickupPointName}")
            Text("Сумма: ${order.totalPrice} ₽")
            order.items.forEach { item ->
                Text("- ${item.bikeModel} x${item.quantity}")
            }

            if (order.status == OrderStatus.IN_RENT) {
                Spacer(Modifier.height(8.dp))
                Text("Осталось времени: ${timerText ?: "..."}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }

            if (order.penalty > 0) {
                Text("Штраф: ${order.penalty} ₽", color = MaterialTheme.colorScheme.error)
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            // Admin / User Actions
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (order.status == OrderStatus.AWAITING_PICKUP) {
                    OutlinedButton(onClick = onCancel) { Text("Отменить") }
                    Button(onClick = onAdminStart, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                        Text("Simulate Pickup")
                    }
                }
                if (order.status == OrderStatus.IN_RENT || order.status == OrderStatus.OVERDUE) {
                    Button(onClick = onAdminComplete, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                        Text("Simulate Return")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: OrderStatus) {
    val (text, color) = when(status) {
        OrderStatus.AWAITING_PICKUP -> "Ожидает выдачи" to Color.Blue
        OrderStatus.IN_RENT -> "В аренде" to Color.Green
        OrderStatus.OVERDUE -> "Просрочено" to Color.Red
        OrderStatus.COMPLETED -> "Завершен" to Color.Gray
        OrderStatus.CANCELED -> "Отменен" to Color.LightGray
    }
    Text(
        text = text,
        color = Color.White,
        modifier = Modifier
            .background(color, MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall
    )
}