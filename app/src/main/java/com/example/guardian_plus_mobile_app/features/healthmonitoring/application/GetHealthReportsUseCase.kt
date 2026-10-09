package com.example.guardian_plus_mobile_app.features.healthmonitoring.application

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.HealthReportRepository
import javax.inject.Inject

class GetHealthReportsUseCase @Inject constructor(
    private val repository: HealthReportRepository
) {
    suspend operator fun invoke(careRecipientProfileId: String): Result<List<HealthReport>> =
        repository.getHealthReports(careRecipientProfileId)
}
