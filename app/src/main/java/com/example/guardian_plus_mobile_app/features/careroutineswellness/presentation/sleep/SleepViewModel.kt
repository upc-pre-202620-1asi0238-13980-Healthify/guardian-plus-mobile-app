package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetSleepCycleRecordsUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SleepUiState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null,
    // Nights of the last week, most recent first
    val nights: List<SleepCycleRecord> = emptyList(),
    val now: Instant = Instant.EPOCH
) {
    val lastNight: SleepCycleRecord? get() = nights.firstOrNull()
}

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val getSleepCycleRecords: GetSleepCycleRecordsUseCase,
    private val serverClock: ServerClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val now = serverClock.now()
            getSleepCycleRecords(DemoSession.CARE_RECIPIENT_PROFILE_ID, from = now - Duration.ofDays(HISTORY_DAYS))
                .onSuccess { nights ->
                    _uiState.update { it.copy(isLoading = false, isLoaded = true, nights = nights, now = now) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar el sueño") }
                }
        }
    }

    private companion object {
        const val HISTORY_DAYS = 7L
    }
}
