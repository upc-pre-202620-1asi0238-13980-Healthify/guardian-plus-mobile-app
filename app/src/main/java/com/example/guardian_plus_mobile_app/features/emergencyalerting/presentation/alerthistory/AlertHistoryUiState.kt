package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import java.time.LocalDate
import java.time.ZoneId

/** Figures of the last 7 days shown above the list. */
data class HistorySummary(
    val totalAlerts: Long,
    // Mean seconds from detection to acknowledgement; null when nothing was acknowledged yet
    val averageResponseSec: Long?,
    val escalatedCount: Int
)

data class AlertHistoryUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val alerts: List<Alert> = emptyList(),
    // null shows every severity
    val severityFilter: Severity? = null,
    val page: Int = 0,
    val isLastPage: Boolean = true,
    val summary: HistorySummary? = null,
    val careRecipientFirstName: String = "",
    val today: LocalDate = LocalDate.now()
) {
    /** Alerts grouped by the local day they were triggered, newest day first, as the list sections. */
    val alertsByDay: List<Pair<LocalDate, List<Alert>>>
        get() = alerts
            .groupBy { it.triggeredAt.atZone(ZoneId.systemDefault()).toLocalDate() }
            .toList()
            .sortedByDescending { (day, _) -> day }
}
