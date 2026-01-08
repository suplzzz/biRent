package com.example.birent.presentation.screen.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.birent.data.local.RentType
import com.example.birent.domain.model.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateToCreateOrder: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                CartEffect.NavigateToCreateOrder -> onNavigateToCreateOrder()
                CartEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Корзина") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (state.items.isNotEmpty()) {
                        IconButton(onClick = { viewModel.processCommand(CartCommand.ClearCart) }) {
                            Icon(Icons.Default.Delete, "Clear")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (state.items.isNotEmpty()) {
                Surface(tonalElevation = 8.dp) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Итого:", style = MaterialTheme.typography.titleLarge)
                            Text("${state.totalPrice} ₽", style = MaterialTheme.typography.titleLarge)
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.processCommand(CartCommand.Checkout) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Оформить заказ")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (state.items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Корзина пуста")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.items) { item ->
                    CartItemCard(
                        item = item,
                        onUpdate = { q, d, t -> viewModel.processCommand(CartCommand.UpdateItem(item.id, q, d, t)) },
                        onRemove = { viewModel.processCommand(CartCommand.RemoveItem(item.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartItem, onUpdate: (Int, Int, RentType) -> Unit, onRemove: () -> Unit) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.bike.model, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "Remove", tint = MaterialTheme.colorScheme.error)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Тариф: ")
                FilterChip(
                    selected = item.rentType == RentType.HOURLY,
                    onClick = { onUpdate(item.quantity, item.duration, RentType.HOURLY) },
                    label = { Text("Почасовой") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = item.rentType == RentType.DAILY,
                    onClick = { onUpdate(item.quantity, item.duration, RentType.DAILY) },
                    label = { Text("Суточный") }
                )
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Кол-во: ")
                IconButton(onClick = { onUpdate(item.quantity - 1, item.duration, item.rentType) }) {
                    Icon(Icons.Default.Remove, null)
                }
                Text("${item.quantity}")
                IconButton(onClick = { onUpdate(item.quantity + 1, item.duration, item.rentType) }) {
                    Icon(Icons.Default.Add, null)
                }
                Spacer(Modifier.weight(1f))
                Text("${item.calculatedPrice} ₽")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (item.rentType == RentType.HOURLY) "Часов: " else "Дней: ")
                IconButton(onClick = { onUpdate(item.quantity, item.duration - 1, item.rentType) }) {
                    Icon(Icons.Default.Remove, null)
                }
                Text("${item.duration}")
                IconButton(onClick = { onUpdate(item.quantity, item.duration + 1, item.rentType) }) {
                    Icon(Icons.Default.Add, null)
                }
                if (item.discountPercent > 0) {
                    Spacer(Modifier.width(8.dp))
                    Text("-${item.discountPercent}%", color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}