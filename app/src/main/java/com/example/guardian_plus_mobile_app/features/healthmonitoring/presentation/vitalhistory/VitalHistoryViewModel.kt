package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetAdherenceUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetAlertHistoryUseCase
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GenerateHealthReportUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetHealthReportsUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetLiveVitalSignsUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetVitalSignHistoryUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.NoReadingsInPeriodException
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReportType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.VitalFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    private val generateHealthReport: GenerateHealthReportUseCase,
    private val getHealthReports: GetHealthReportsUseCase,
    private val getAlertHistory: GetAlertHistoryUseCase,
    private val getAdherence: GetAdherenceUseCase,
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

    /**
     * "Reporte semanal": the platform compiles one every Sunday on its own (US24). The latest one is shown if it
     * covers the last week; otherwise the last seven days are compiled now. The sheet adds the alerts triggered
     * and the medication adherence of the same days.
     */
    fun openWeeklyReport() {
        if (_uiState.value.busyAction != null) return
        val today = _uiState.value.today
        val zone = _uiState.value.zone
        viewModelScope.launch {
            _uiState.update { it.copy(busyAction = ReportAction.WEEKLY_REPORT) }
            val latestWeekly = getHealthReports(DemoSession.CARE_RECIPIENT_PROFILE_ID).getOrNull()
                ?.firstOrNull { it.reportType == HealthReportType.WEEKLY_AUTOMATIC && it.periodEnd >= today.minusDays(WEEK_DAYS) }
            val result = latestWeekly?.let { Result.success(it) } ?: generateHealthReport(
                DemoSession.CARE_RECIPIENT_PROFILE_ID,
                DemoSession.CURRENT_USER_ID,
                today.minusDays(WEEK_DAYS - 1),
                today
            )
            result
                .onSuccess { report ->
                    // Both are extras of the sheet, so they are asked together and a failure only blanks its own box
                    val (alerts, adherence) = coroutineScope {
                        val alerts = async {
                            getAlertHistory(
                                DemoSession.CARE_RECIPIENT_PROFILE_ID,
                                from = report.periodStart.atStartOfDay(zone).toInstant(),
                                to = report.periodEnd.plusDays(1).atStartOfDay(zone).toInstant(),
                                size = 1
                            ).getOrNull()?.totalElements
                        }
                        val adherence = async {
                            getAdherence(DemoSession.CARE_RECIPIENT_PROFILE_ID, ReminderType.MEDICATION, report.periodStart, report.periodEnd)
                                .getOrNull()
                        }
                        alerts.await() to adherence.await()
                    }
                    _uiState.update {
                        it.copy(busyAction = null, weeklyReport = WeeklyReport(report, alerts, adherence))
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(busyAction = null, actionMessage = e.message ?: "No se pudo generar el reporte") }
                }
        }
    }

    fun closeWeeklyReport() {
        _uiState.update { it.copy(weeklyReport = null) }
    }

    fun openExportSheet() {
        _uiState.update { it.copy(isExportSheetOpen = true) }
    }

    fun closeExportSheet() {
        _uiState.update { it.copy(isExportSheetOpen = false) }
    }

    /** "Generar y exportar PDF": the platform compiles the range, then the screen draws the chosen signs. */
    fun exportRecord(range: ExportRange, metrics: Set<VitalSignType>) {
        if (_uiState.value.busyAction != null || metrics.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(busyAction = ReportAction.EXPORT_PDF) }
            generateHealthReport(DemoSession.CARE_RECIPIENT_PROFILE_ID, DemoSession.CURRENT_USER_ID, range.start, range.end)
                .onSuccess { report ->
                    _uiState.update {
                        it.copy(busyAction = null, isExportSheetOpen = false, reportToExport = ExportRequest(report, metrics))
                    }
                }
                .onFailure { e ->
                    // An empty range stays open and marked, so another one can be picked right away
                    _uiState.update {
                        it.copy(
                            busyAction = null,
                            emptyRanges = if (e is NoReadingsInPeriodException) it.emptyRanges + range else it.emptyRanges,
                            actionMessage = e.message ?: "No se pudo generar el reporte"
                        )
                    }
                }
        }
    }

    fun onReportExported() {
        _uiState.update { it.copy(reportToExport = null) }
    }

    fun onActionMessageShown() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    // Every type of the period is already loaded, so only a new period goes back to the platform
    fun applyFilter(filter: VitalFilter) {
        val previous = _uiState.value.filter
        _uiState.update { it.copy(filter = filter) }
        if (filter.period != previous.period) load()
    }

    private companion object {
        const val WEEK_DAYS = 7L
    }
}
