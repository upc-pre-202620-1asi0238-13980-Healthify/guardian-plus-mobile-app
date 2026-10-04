package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.AcknowledgeAlertUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.ClaimAlertResponseUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.CloseIncidentUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.CompleteAlertResponseUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetAlertByIdUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetAlertContextUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetAlertSettingsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetEmergencyContactsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetIncidentByAlertIdUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.StabilizeIncidentUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AlertDetailViewModel @Inject constructor(
    private val getAlertById: GetAlertByIdUseCase,
    private val getIncidentByAlertId: GetIncidentByAlertIdUseCase,
    private val getEmergencyContacts: GetEmergencyContactsUseCase,
    private val getAlertSettings: GetAlertSettingsUseCase,
    private val getAlertContext: GetAlertContextUseCase,
    private val acknowledgeAlert: AcknowledgeAlertUseCase,
    private val claimAlertResponse: ClaimAlertResponseUseCase,
    private val completeAlertResponse: CompleteAlertResponseUseCase,
    private val stabilizeIncident: StabilizeIncidentUseCase,
    private val closeIncident: CloseIncidentUseCase,
    // Countdowns follow the backend clock, which decides when an alert escalates
    private val serverClock: ServerClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AlertDetailUiState(
            careRecipientFirstName = DemoSession.CARE_RECIPIENT_FIRST_NAME,
            currentUserId = DemoSession.CURRENT_USER_ID
        )
    )
    val uiState: StateFlow<AlertDetailUiState> = _uiState.asStateFlow()

    private var alertId: String? = null
    private var tickerJob: Job? = null

    fun load(alertId: String) {
        this.alertId = alertId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getEmergencyContacts(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .onSuccess { contacts -> _uiState.update { it.copy(contacts = contacts) } }
            getAlertSettings(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .onSuccess { settings ->
                    _uiState.update {
                        it.copy(ackTimeoutSec = settings.primaryAckTimeoutSec, escalationEnabled = settings.escalationEnabled)
                    }
                }
            refresh()
        }
        startTicker()
    }

    fun acknowledge() = runAction(AlertDetailAction.ACKNOWLEDGE) { alertId ->
        acknowledgeAlert(alertId, DemoSession.CURRENT_USER_ID)
    }

    fun claimResponse() = runAction(AlertDetailAction.CLAIM) { alertId ->
        claimAlertResponse(alertId, DemoSession.CURRENT_USER_ID)
    }

    fun completeResponse() {
        val responseId = _uiState.value.myActiveResponseId ?: return
        runAction(AlertDetailAction.COMPLETE) { alertId -> completeAlertResponse(alertId, responseId, null) }
    }

    fun stabilize(notes: String) {
        val incidentId = _uiState.value.incident?.id ?: return
        runAction(AlertDetailAction.STABILIZE) { stabilizeIncident(incidentId, notes.ifBlank { null }) }
    }

    fun close(notes: String) {
        val incidentId = _uiState.value.incident?.id ?: return
        runAction(AlertDetailAction.CLOSE) { closeIncident(incidentId, notes.ifBlank { null }) }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorMessage = null) }
    }

    /** Runs a command, then reloads the alert and its incident so every card shows the new state. */
    private fun runAction(action: AlertDetailAction, command: suspend (alertId: String) -> Result<*>) {
        val alertId = alertId ?: return
        if (_uiState.value.runningAction != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(runningAction = action) }
            command(alertId)
                .onSuccess {
                    refresh()
                    _uiState.update { it.copy(runningAction = null) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(runningAction = null, actionErrorMessage = e.message ?: "No se pudo completar la acción")
                    }
                }
        }
    }

    private suspend fun refresh() {
        val alertId = alertId ?: return
        getAlertById(alertId)
            .onSuccess { alert ->
                val incident = getIncidentByAlertId(alertId).getOrNull()
                val context = getAlertContext(alert).getOrDefault(AlertContext())
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        alert = alert,
                        incident = incident,
                        context = context,
                        now = serverClock.now()
                    )
                }
            }
            .onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar la alerta") }
            }
    }

    // Ticks the clock every second and, while the alert is still in play, re-reads it every few seconds
    // because escalations and other members' answers happen on the server
    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            var seconds = 0
            while (true) {
                delay(CLOCK_TICK_MS)
                seconds++
                _uiState.update { it.copy(now = serverClock.now()) }
                val state = _uiState.value
                if (seconds % REFRESH_EVERY_SECONDS == 0 && !state.isFinished && state.runningAction == null) {
                    refresh()
                }
            }
        }
    }

    private companion object {
        const val CLOCK_TICK_MS = 1_000L
        const val REFRESH_EVERY_SECONDS = 5
    }
}
