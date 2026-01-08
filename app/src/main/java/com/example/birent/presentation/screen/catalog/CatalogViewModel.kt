package com.example.birent.presentation.screen.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birent.data.local.BikeType
import com.example.birent.data.local.RentType
import com.example.birent.domain.model.Bike
import com.example.birent.domain.repository.BikeRepository
import com.example.birent.domain.usecase.cart.ManageCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val bikeRepository: BikeRepository,
    private val manageCartUseCase: ManageCartUseCase
) : ViewModel() {

    private val _filterState = MutableStateFlow(CatalogFilterState())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<CatalogState> = _filterState
        .flatMapLatest { filters ->
            bikeRepository.searchBikes(
                query = filters.query.takeIf { it.isNotBlank() },
                type = filters.selectedType,
                minPrice = filters.minPrice,
                maxPrice = filters.maxPrice
            ).map { bikes ->
                CatalogState(
                    isLoading = false,
                    bikes = bikes,
                    filterState = filters
                )
            }.onStart {
                emit(CatalogState(isLoading = true, filterState = filters))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogState(isLoading = true))

    private val _effect = Channel<CatalogEffect>()
    val effect = _effect.receiveAsFlow()

    fun processCommand(command: CatalogCommand) {
        when (command) {
            is CatalogCommand.Search -> _filterState.update { it.copy(query = command.query) }
            is CatalogCommand.FilterType -> _filterState.update { it.copy(selectedType = command.type) }
            is CatalogCommand.AddToCart -> addToCart(command.bikeId)
            CatalogCommand.ToggleFilters -> _filterState.update { it.copy(isFilterVisible = !it.isFilterVisible) }
            is CatalogCommand.SetPriceRange -> _filterState.update { it.copy(minPrice = command.min, maxPrice = command.max) }
        }
    }

    private fun addToCart(bikeId: Long) {
        viewModelScope.launch {
            manageCartUseCase.addToCart(bikeId, 1, RentType.HOURLY, 1)
            _effect.send(CatalogEffect.ShowToast("Добавлено в корзину"))
        }
    }
}

data class CatalogFilterState(
    val query: String = "",
    val selectedType: BikeType? = null,
    val isFilterVisible: Boolean = false,
    val minPrice: Double? = null,
    val maxPrice: Double? = null
)

data class CatalogState(
    val isLoading: Boolean = false,
    val bikes: List<Bike> = emptyList(),
    val filterState: CatalogFilterState = CatalogFilterState()
)

sealed interface CatalogCommand {
    data class Search(val query: String) : CatalogCommand
    data class FilterType(val type: BikeType?) : CatalogCommand
    data object ToggleFilters : CatalogCommand
    data class SetPriceRange(val min: Double?, val max: Double?) : CatalogCommand
    data class AddToCart(val bikeId: Long) : CatalogCommand
}

sealed interface CatalogEffect {
    data class ShowToast(val message: String) : CatalogEffect
}