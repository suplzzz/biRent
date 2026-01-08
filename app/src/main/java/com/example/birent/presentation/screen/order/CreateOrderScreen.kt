package com.example.birent.presentation.screen.order

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreateOrderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                CreateOrderEffect.OrderCreated -> {
                    Toast.makeText(context, "Заказ оформлен!", Toast.LENGTH_SHORT).show()
                    onNavigateToProfile()
                }
                is CreateOrderEffect.Error -> Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Оформление заказа") }) },
        bottomBar = {
            Button(
                onClick = { viewModel.submitOrder() },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = state.selectedPointId != null
            ) {
                Text("Подтвердить")
            }
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            Text("Выберите пункт выдачи:", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(state.pickupPoints) { point ->
                    ListItem(
                        headlineContent = { Text(point.name) },
                        supportingContent = { Text(point.address) },
                        leadingContent = {
                            RadioButton(
                                selected = state.selectedPointId == point.id,
                                onClick = null
                            )
                        },
                        modifier = Modifier.clickable { viewModel.selectPoint(point.id) }
                    )
                }
            }
        }
    }
}