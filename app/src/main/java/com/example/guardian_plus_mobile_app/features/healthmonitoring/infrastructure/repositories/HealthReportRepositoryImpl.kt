package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReportType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.NoReadingsInPeriodException
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.StabilityIndex
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignSummary
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.HealthReportRepository
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto.GenerateHealthReportRequestDto
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto.HealthReportDto
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.services.HealthReportService
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class HealthReportRepositoryImpl @Inject constructor(
    private val service: HealthReportService
) : HealthReportRepository {

    override suspend fun generateHealthReport(
        careRecipientProfileId: String,
        generatedByUserId: String,
        periodStart: LocalDate,
        periodEnd: LocalDate
    ): Result<HealthReport> = apiCall(
        request = {
            service.generateHealthReport(
                GenerateHealthReportRequestDto(
                    careRecipientProfileId = careRecipientProfileId,
                    generatedByUserId = generatedByUserId,
                    periodStart = periodStart.toString(),
                    periodEnd = periodEnd.toString()
                )
            )
        },
        // 422 is the platform's "no readings in the selected period", which the screens show as "sin datos"
        onError = { code, message -> if (code == HTTP_UNPROCESSABLE) NoReadingsInPeriodException(message) else Exception(message) }
    ) { it.toDomain() }

    // Newest first, the order a caregiver looks for them in
    override suspend fun getHealthReports(careRecipientProfileId: String): Result<List<HealthReport>> =
        apiCall({ service.getHealthReports(careRecipientProfileId) }) { dtos ->
            dtos.map { it.toDomain() }.sortedByDescending { it.generatedAt }
        }
}

private const val HTTP_UNPROCESSABLE = 422

private fun HealthReportDto.toDomain() = HealthReport(
    id = id,
    careRecipientProfileId = careRecipientProfileId,
    reportType = HealthReportType.valueOf(reportType),
    periodStart = LocalDate.parse(periodStart),
    periodEnd = LocalDate.parse(periodEnd),
    // A sign the app does not know yet is left out rather than failing the whole report
    summaries = summaries.mapNotNull { dto ->
        val type = VitalSignType.entries.firstOrNull { it.name == dto.metricType } ?: return@mapNotNull null
        VitalSignSummary(
            type = type,
            averageValue = dto.averageValue,
            minValue = dto.minValue,
            maxValue = dto.maxValue,
            readingsCount = dto.readingsCount,
            outOfRangeCount = dto.outOfRangeCount,
            stability = StabilityIndex.valueOf(dto.stabilityIndex)
        )
    },
    recurrentAnomaliesCount = recurrentAnomaliesCount,
    clinicallyStable = clinicallyStable,
    generatedAt = Instant.parse(generatedAt)
)
