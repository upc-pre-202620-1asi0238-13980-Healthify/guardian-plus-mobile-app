package com.example.guardian_plus_mobile_app.features.healthmonitoring.application

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.HealthReportRepository
import java.time.LocalDate
import javax.inject.Inject

class GenerateHealthReportUseCase @Inject constructor(
    private val repository: HealthReportRepository
) {
    suspend operator fun invoke(
        careRecipientProfileId: String,
        generatedByUserId: String,
        periodStart: LocalDate,
        periodEnd: LocalDate
    ): Result<HealthReport> = repository.generateHealthReport(careRecipientProfileId, generatedByUserId, periodStart, periodEnd)
}
