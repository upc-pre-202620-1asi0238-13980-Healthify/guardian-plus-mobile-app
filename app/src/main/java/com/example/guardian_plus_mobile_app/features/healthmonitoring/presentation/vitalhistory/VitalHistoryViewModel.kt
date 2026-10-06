package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetLiveVitalSignsUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetVitalSignHistoryUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val today = serverClock.now().atZone(_uiState.value.zone).toLocalDate()

            // Ranges only colour the badges; without them the history still makes sense
            val ranges = getLiveVitalSigns(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .getOrNull()
                ?.vitalSigns
                ?.associate { it.type to it.normalMinimum..it.normalMaximum }
                .orEmpty()

            // The platform filters by UTC date while the week is drawn on Lima's calendar (UTC−5), so one extra
            // day is asked on each side and the state keeps only the readings that fall inside the local week
            getVitalSignHistory(DemoSession.CARE_RECIPIENT_PROFILE_ID, today.minusDays(7), today.plusDays(1))
                .onSuccess { readings ->
                    _uiState.update {
                        it.copy(isLoading = false, readings = readings, ranges = ranges, today = today)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar el historial")
                    }
                }
        }
    }

    // All types of the week are already loaded, so switching is local
    fun selectType(type: VitalSignType) {
        _uiState.update { it.copy(selectedType = type) }
    }
}
