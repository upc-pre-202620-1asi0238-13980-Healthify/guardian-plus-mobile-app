package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetAlertHistoryUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AlertHistoryViewModel @Inject constructor(
    private val getAlertHistory: GetAlertHistoryUseCase,
    private val serverClock: ServerClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AlertHistoryUiState(careRecipientFirstName = DemoSession.CARE_RECIPIENT_FIRST_NAME)
    )
    val uiState: StateFlow<AlertHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            loadFirstPage()
            loadSummary()
        }
    }

    /** Pull to refresh: keeps the current list on screen while the new one loads. */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadFirstPage()
            loadSummary()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun selectSeverity(severity: Severity?) {
        if (severity == _uiState.value.severityFilter) return
        _uiState.update { it.copy(severityFilter = severity, alerts = emptyList(), isLoading = true, errorMessage = null) }
        viewModelScope.launch { loadFirstPage() }
    }

    /** Called when the list reaches its end; asks for the next page unless it was the last one. */
    fun loadMore() {
        val state = _uiState.value
        if (state.isLastPage || state.isLoadingMore || state.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            getAlertHistory(
                careRecipientProfileId = DemoSession.CARE_RECIPIENT_PROFILE_ID,
                severity = state.severityFilter,
                page = state.page + 1
            )
                .onSuccess { page ->
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            alerts = (it.alerts + page.alerts).distinctBy { alert -> alert.id },
                            page = page.page,
                            isLastPage = page.isLast
                        )
                    }
                }
                .onFailure {
                    // The list already on screen stays usable; scrolling again retries
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
        }
    }

    private suspend fun loadFirstPage() {
        val severity = _uiState.value.severityFilter
        getAlertHistory(careRecipientProfileId = DemoSession.CARE_RECIPIENT_PROFILE_ID, severity = severity)
            .onSuccess { page ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        alerts = page.alerts,
                        page = page.page,
                        isLastPage = page.isLast,
                        today = LocalDate.now(ZoneId.systemDefault())
                    )
                }
            }
            .onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar el historial") }
            }
    }

    // Always over every severity: the summary describes the week, not the current filter
    private suspend fun loadSummary() {
        getAlertHistory(
            careRecipientProfileId = DemoSession.CARE_RECIPIENT_PROFILE_ID,
            from = serverClock.now().minus(Duration.ofDays(SUMMARY_DAYS)),
            size = GetAlertHistoryUseCase.MAX_PAGE_SIZE
        ).onSuccess { page ->
            _uiState.update { it.copy(summary = summarize(page.alerts, page.totalElements)) }
        }
    }

    private fun summarize(alerts: List<Alert>, total: Long): HistorySummary {
        val responseTimes = alerts.mapNotNull { alert ->
            alert.acknowledgedAt?.let { Duration.between(alert.triggeredAt, it).seconds }
        }
        return HistorySummary(
            totalAlerts = total,
            averageResponseSec = if (responseTimes.isEmpty()) null else responseTimes.average().toLong(),
            // An alert that reached secondary contacts or everyone was escalated at least once
            escalatedCount = alerts.count {
                it.currentRecipientLevel == RecipientLevel.SECONDARY || it.currentRecipientLevel == RecipientLevel.BROADCAST
            }
        )
    }

    private companion object {
        const val SUMMARY_DAYS = 7L
    }
}
