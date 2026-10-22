package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.inactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.ConfigureInactivityDetectionUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetActivityLogUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetActivityMonitorUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityLogEntry
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitor
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InactivityUiState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null,
    // Null until the wristband reports activity or detection is configured
    val monitor: ActivityMonitor? = null,
    val detectionEnabled: Boolean = false,
    val thresholdMinutes: Int = DEFAULT_THRESHOLD_MINUTES,
    val recentActivity: List<ActivityLogEntry> = emptyList(),
    val now: Instant = Instant.EPOCH,
    val isSaving: Boolean = false,
    val message: RoutineMessage? = null
) {
    companion object {
        const val DEFAULT_THRESHOLD_MINUTES = 60
    }
}

@HiltViewModel
class InactivityViewModel @Inject constructor(
    private val getActivityMonitor: GetActivityMonitorUseCase,
    private val getActivityLog: GetActivityLogUseCase,
    private val configureInactivityDetection: ConfigureInactivityDetectionUseCase,
    private val serverClock: ServerClock,
    private val careContextStore: CareContextStore
) : ViewModel() {

    // Whose data this screen shows: the signed-in account and the person under its care
    private val careContext = careContextStore.require()

    private val _uiState = MutableStateFlow(InactivityUiState())
    val uiState: StateFlow<InactivityUiState> = _uiState.asStateFlow()

    init {
        load()
        startAutoRefresh()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            refresh()
        }
    }

    fun setDetectionEnabled(enabled: Boolean) = save(enabled, _uiState.value.thresholdMinutes)

    fun setThreshold(minutes: Int) = save(_uiState.value.detectionEnabled, minutes)

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }

    // The control moves at once and goes back if the platform rejects the change
    private fun save(enabled: Boolean, thresholdMinutes: Int) {
        val state = _uiState.value
        if (state.isSaving) return
        _uiState.update { it.copy(detectionEnabled = enabled, thresholdMinutes = thresholdMinutes, isSaving = true) }
        viewModelScope.launch {
            configureInactivityDetection(careContext.careRecipientProfileId, enabled, thresholdMinutes)
                .onSuccess { monitor -> _uiState.update { it.withMonitor(monitor).copy(isSaving = false) } }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            detectionEnabled = state.detectionEnabled,
                            thresholdMinutes = state.thresholdMinutes,
                            isSaving = false,
                            message = RoutineMessage.Text(e.message ?: "No se pudo guardar la detección de inactividad")
                        )
                    }
                }
        }
    }

    private suspend fun refresh() = coroutineScope {
        val personUnderCareId = careContext.careRecipientProfileId
        val log = async { getActivityLog(personUnderCareId, RECENT_ACTIVITY_LIMIT) }
        getActivityMonitor(personUnderCareId)
            .onSuccess { monitor ->
                _uiState.update {
                    val updated = if (monitor != null) it.withMonitor(monitor) else it
                    updated.copy(
                        isLoading = false,
                        isLoaded = true,
                        errorMessage = null,
                        recentActivity = log.await().getOrDefault(emptyList()),
                        now = serverClock.now()
                    )
                }
            }
            .onFailure { e ->
                log.cancel()
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar la actividad") }
            }
    }

    // The counter of minutes without movement keeps changing with the wristband's samples
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_MS)
                if (!_uiState.value.isLoading && !_uiState.value.isSaving) refresh()
            }
        }
    }

    private fun InactivityUiState.withMonitor(monitor: ActivityMonitor) = copy(
        monitor = monitor,
        detectionEnabled = monitor.detectionEnabled,
        thresholdMinutes = monitor.thresholdMinutes
    )

    private companion object {
        const val RECENT_ACTIVITY_LIMIT = 10
        const val AUTO_REFRESH_MS = 30_000L
    }
}
