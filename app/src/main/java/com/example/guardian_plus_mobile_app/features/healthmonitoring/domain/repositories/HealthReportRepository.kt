package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import java.time.LocalDate

interface HealthReportRepository {
    suspend fun generateHealthReport(
        careRecipientProfileId: String,
        generatedByUserId: String,
        periodStart: LocalDate,
        periodEnd: LocalDate
    ): Result<HealthReport>

    suspend fun getHealthReports(careRecipientProfileId: String): Result<List<HealthReport>>
}
