package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport

data class HealthReportUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val report: HealthReport? = null,
    // Newest first, without the one on screen
    val earlierReports: List<HealthReport> = emptyList()
)
