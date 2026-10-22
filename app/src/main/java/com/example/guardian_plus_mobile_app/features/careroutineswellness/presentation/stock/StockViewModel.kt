package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.ConfirmMedicationAcquisitionUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetMedicationStocksUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StockUiState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null,
    val stocks: List<MedicationStock> = emptyList(),
    // Stock receiving a new package, so only its button shows progress
    val restockingId: String? = null,
    val message: RoutineMessage? = null
) {
    /** Medications that run out within the restock threshold, the first one leading the warning. */
    val lowStocks: List<MedicationStock> get() = stocks.filter { it.restockRecommended }.sortedBy { it.remainingDaysOfSupply }
}

@HiltViewModel
class StockViewModel @Inject constructor(
    private val getMedicationStocks: GetMedicationStocksUseCase,
    private val confirmMedicationAcquisition: ConfirmMedicationAcquisitionUseCase,
    private val careContextStore: CareContextStore
) : ViewModel() {

    // Whose data this screen shows: the signed-in account and the person under its care
    private val careContext = careContextStore.require()

    private val _uiState = MutableStateFlow(StockUiState())
    val uiState: StateFlow<StockUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getMedicationStocks(careContext.careRecipientProfileId)
                .onSuccess { stocks -> _uiState.update { it.copy(isLoading = false, isLoaded = true, stocks = stocks) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar el stock") }
                }
        }
    }

    /** "Añadir envase": the platform adds a whole package and recalculates the days of supply. */
    fun addPackage(stock: MedicationStock) {
        if (_uiState.value.restockingId != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(restockingId = stock.id) }
            confirmMedicationAcquisition(stock.id)
                .onSuccess { updated ->
                    _uiState.update { state ->
                        state.copy(
                            stocks = state.stocks.map { if (it.id == updated.id) updated else it },
                            message = RoutineMessage.Resource(R.string.stock_added_message, listOf(updated.remainingDoses - stock.remainingDoses))
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(message = RoutineMessage.Text(e.message ?: "No se pudo añadir el envase")) }
                }
            _uiState.update { it.copy(restockingId = null) }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }
}
