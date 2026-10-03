package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.AcknowledgeAlertUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetActiveAlertsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetAlertByIdUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetAlertContextUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetAlertSettingsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetPendingAlertsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ActiveAlertsViewModel @Inject constructor(
    private val getActiveAlerts: GetActiveAlertsUseCase,
    private val getPendingAlerts: GetPendingAlertsUseCase,
    private val getAlertById: GetAlertByIdUseCase,
    private val acknowledgeAlert: AcknowledgeAlertUseCase,
    private val getAlertSettings: GetAlertSettingsUseCase,
    private val getAlertContext: GetAlertContextUseCase,
    // Countdowns follow the backend clock, which decides when an alert escalates
    private val serverClock: ServerClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ActiveAlertsUiState(careRecipientFirstName = DemoSession.CARE_RECIPIENT_FIRST_NAME)
    )
    val uiState: StateFlow<ActiveAlertsUiState> = _uiState.asStateFlow()

    init {
        loadAlerts()
        startClock()
        startAutoRefresh()
    }

    fun loadAlerts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            refresh()
        }
    }

    fun acknowledge(alertId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(acknowledgingAlertId = alertId) }
            acknowledgeAlert(alertId, DemoSession.CURRENT_USER_ID)
                .onSuccess {
                    _uiState.update { it.copy(acknowledgingAlertId = null, acknowledgedAlertId = alertId) }
                    refresh()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(acknowledgingAlertId = null, actionErrorMessage = e.message ?: "No se pudo reconocer la alerta")
                    }
                }
        }
    }

    fun onAcknowledgedAlertOpened() {
        _uiState.update { it.copy(acknowledgedAlertId = null) }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorMessage = null) }
    }

    private suspend fun refresh() {
        getAlertSettings(DemoSession.CARE_RECIPIENT_PROFILE_ID)
            .onSuccess { settings ->
                _uiState.update {
                    it.copy(ackTimeoutSec = settings.primaryAckTimeoutSec, escalationEnabled = settings.escalationEnabled)
                }
            }

        getActiveAlerts(DemoSession.CARE_RECIPIENT_PROFILE_ID)
            .onSuccess { alerts ->
                val myPendingIds = getPendingAlerts(DemoSession.CURRENT_USER_ID)
                    .getOrDefault(emptyList())
                    .map { it.id }
                    .toSet()
                val items = alerts
                    .sortedWith(byUrgency)
                    .map { alert ->
                        ActiveAlertItem(
                            alert = alert,
                            context = getAlertContext(alert).getOrDefault(AlertContext()),
                            awaitsMyAcknowledgement = alert.id in myPendingIds
                        )
                    }
                val featured = items.firstOrNull { it.isFeatured() }?.withDispatchTime()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        now = serverClock.now(),
                        featuredAlert = featured,
                        otherAlerts = items.filterNot { item -> item.alert.id == featured?.alert?.id }
                    )
                }
            }
            .onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudieron cargar las alertas") }
            }
    }

    /** A critical alert still in play: waiting for the person under care or for an acknowledgement. */
    private fun ActiveAlertItem.isFeatured(): Boolean =
        alert.severity == Severity.CRITICAL &&
            (alert.status == AlertStatus.PENDING_CONFIRMATION || alert.status.isAwaitingAcknowledgement)

    /** List summaries do not carry lastDispatchedAt, which the countdown needs, so the full alert is read. */
    private suspend fun ActiveAlertItem.withDispatchTime(): ActiveAlertItem {
        var item = this
        getAlertById(alert.id).onSuccess { fullAlert -> item = copy(alert = fullAlert) }
        return item
    }

    private fun startClock() {
        viewModelScope.launch {
            while (true) {
                delay(CLOCK_TICK_MS)
                _uiState.update { it.copy(now = serverClock.now()) }
            }
        }
    }

    // Escalations and new alerts happen on the server, so the list is refreshed periodically
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_MS)
                if (!_uiState.value.isLoading) refresh()
            }
        }
    }

    private companion object {
        const val CLOCK_TICK_MS = 1_000L
        const val AUTO_REFRESH_MS = 10_000L

        // Critical first, then what still waits for an acknowledgement, then the most recent
        val byUrgency = compareBy<Alert> { it.severity.ordinal }
            .thenBy { if (it.status.isAwaitingAcknowledgement) 0 else 1 }
            .thenByDescending { it.triggeredAt }
    }
}
