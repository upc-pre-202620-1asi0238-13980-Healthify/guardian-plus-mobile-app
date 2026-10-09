package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetHealthReportUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetHealthReportsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HealthReportViewModel @Inject constructor(
    private val getHealthReport: GetHealthReportUseCase,
    private val getHealthReports: GetHealthReportsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthReportUiState())
    val uiState: StateFlow<HealthReportUiState> = _uiState.asStateFlow()

    fun load(reportId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getHealthReport(reportId)
                .onSuccess { report -> _uiState.update { it.copy(isLoading = false, report = report) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar el reporte") }
                }
            // The list is a bonus: without it the report still stands on its own
            getHealthReports(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .onSuccess { reports -> _uiState.update { it.copy(earlierReports = reports.filter { report -> report.id != reportId }) } }
        }
    }
}
