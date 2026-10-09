package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetLiveVitalSignsUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetVitalSignHistoryUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.VitalFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VitalHistoryViewModel @Inject constructor(
    private val getVitalSignHistory: GetVitalSignHistoryUseCase,
    private val getLiveVitalSigns: GetLiveVitalSignsUseCase,
    private val serverClock: ServerClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(VitalHistoryUiState())
    val uiState: StateFlow<VitalHistoryUiState> = _uiState.asStateFlow()

    // A newer period replaces a load still running, so an older answer cannot land on top of it
    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        val period = _uiState.value.filter.period
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val today = serverClock.now().atZone(_uiState.value.zone).toLocalDate()

            // Ranges only colour the badges; without them the history still makes sense
            val ranges = getLiveVitalSigns(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .getOrNull()
                ?.vitalSigns
                ?.associate { it.type to it.normalMinimum..it.normalMaximum }
                .orEmpty()

            // The platform filters by UTC date while the period is drawn on Lima's calendar (UTC−5), so one extra
            // day is asked on each side and the state keeps only the readings that fall inside the local period
            getVitalSignHistory(
                DemoSession.CARE_RECIPIENT_PROFILE_ID,
                today.minusDays(period.dayCount.toLong()),
                today.plusDays(1)
            )
                .onSuccess { readings ->
                    _uiState.update {
                        it.copy(isLoading = false, readings = readings, ranges = ranges, period = period, today = today)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar el historial")
                    }
                }
        }
    }

    // Every type of the period is already loaded, so switching is local
    fun selectType(type: VitalSignType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    // Every type of the period is already loaded, so only a new period goes back to the platform
    fun applyFilter(filter: VitalFilter) {
        val previous = _uiState.value.filter
        _uiState.update { it.copy(filter = filter) }
        if (filter.period != previous.period) load()
    }
}
