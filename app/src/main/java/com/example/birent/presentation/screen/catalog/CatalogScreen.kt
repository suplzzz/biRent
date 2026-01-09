package com.example.birent.presentation.screen.catalog

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.birent.data.local.BikeType
import com.example.birent.domain.model.Bike
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            if (it is CatalogEffect.ShowToast) {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Каталог велосипедов") })
                SearchBar(
                    query = state.filterState.query,
                    onQueryChange = { viewModel.processCommand(CatalogCommand.Search(it)) },
                    onToggleFilter = { viewModel.processCommand(CatalogCommand.ToggleFilters) }
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            AnimatedVisibility(visible = state.filterState.isFilterVisible) {
                FiltersSection(
                    filterState = state.filterState,
                    onTypeSelect = { viewModel.processCommand(CatalogCommand.FilterType(it)) },
                    onFrameSelect = { viewModel.processCommand(CatalogCommand.FilterFrameSize(it)) },
                    onSpeedChange = { min, max -> viewModel.processCommand(CatalogCommand.SetSpeedRange(min, max)) },
                    onPriceChange = { min, max -> viewModel.processCommand(CatalogCommand.SetPriceRange(min, max)) }
                )
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.bikes) { bike ->
                        BikeCard(bike, onAdd = { viewModel.processCommand(CatalogCommand.AddToCart(it)) })
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onToggleFilter: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Поиск модели...") },
            leadingIcon = { Icon(Icons.Default.Search, null) }
        )
        IconButton(onClick = onToggleFilter) {
            Icon(Icons.Default.FilterList, "Filter")
        }
    }
}

@Composable
fun FiltersSection(
    filterState: CatalogFilterState,
    onTypeSelect: (BikeType?) -> Unit,
    onFrameSelect: (String?) -> Unit,
    onSpeedChange: (Int, Int) -> Unit,
    onPriceChange: (Double, Double) -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        // 1. Тип
        Text("Тип велосипеда", style = MaterialTheme.typography.labelLarge)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = filterState.selectedType == null,
                    onClick = { onTypeSelect(null) },
                    label = { Text("Все") }
                )
            }
            items(BikeType.values()) { type ->
                FilterChip(
                    selected = filterState.selectedType == type,
                    onClick = { onTypeSelect(type) },
                    label = { Text(type.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Размер рамы (Характеристика)
        Text("Размер рамы", style = MaterialTheme.typography.labelLarge)
        val frameSizes = listOf("M", "L", "19 inch", "Universal")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = filterState.selectedFrameSize == null,
                    onClick = { onFrameSelect(null) },
                    label = { Text("Любой") }
                )
            }
            items(frameSizes) { size ->
                FilterChip(
                    selected = filterState.selectedFrameSize == size,
                    onClick = { onFrameSelect(size) },
                    label = { Text(size) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Скорости (Характеристика - диапазон)
        Text("Скорости: ${filterState.minSpeeds} - ${filterState.maxSpeeds}", style = MaterialTheme.typography.labelLarge)
        RangeSlider(
            value = filterState.minSpeeds.toFloat()..filterState.maxSpeeds.toFloat(),
            onValueChange = { range ->
                onSpeedChange(range.start.roundToInt(), range.endInclusive.roundToInt())
            },
            valueRange = 0f..30f,
            steps = 29
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Цена за час (Диапазон)
        Text("Цена (час): ${filterState.minPrice.toInt()} - ${filterState.maxPrice.toInt()} ₽", style = MaterialTheme.typography.labelLarge)
        RangeSlider(
            value = filterState.minPrice.toFloat()..filterState.maxPrice.toFloat(),
            onValueChange = { range ->
                onPriceChange(range.start.toDouble(), range.endInclusive.toDouble())
            },
            valueRange = 0f..1000f,
            steps = 9
        )
    }
}

@Composable
fun BikeCard(bike: Bike, onAdd: (Long) -> Unit) {
    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(bike.model, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (bike.isAvailable) {
                    Text("В наличии", color = MaterialTheme.colorScheme.primary)
                } else {
                    Text("Нет в наличии", color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text("${bike.type.name} • ${bike.speeds} ск. • Рама ${bike.frameSize}", style = MaterialTheme.typography.bodyMedium)
            Text("Цвет: ${bike.color}")
            Spacer(Modifier.height(8.dp))
            Text("${bike.priceHour} ₽/час • ${bike.priceDay} ₽/сутки", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onAdd(bike.id) },
                enabled = bike.isAvailable,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("В корзину")
            }
        }
    }
}