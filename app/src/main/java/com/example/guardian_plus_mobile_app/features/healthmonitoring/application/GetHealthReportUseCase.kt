package com.example.guardian_plus_mobile_app.features.healthmonitoring.application

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.HealthReportRepository
import javax.inject.Inject

class GetHealthReportUseCase @Inject constructor(
    private val repository: HealthReportRepository
) {
    suspend operator fun invoke(reportId: String): Result<HealthReport> = repository.getHealthReport(reportId)
}
