package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReportType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.StabilityIndex
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignSummary
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import java.time.Instant
import java.time.LocalDate

// Fake report for the @Preview functions of the report screen

internal fun previewHealthReport(
    id: String = "report",
    periodEnd: LocalDate = LocalDate.parse("2026-10-09"),
    reportType: HealthReportType = HealthReportType.ON_DEMAND
): HealthReport {
    fun summary(type: VitalSignType, average: Double, min: Double, max: Double, outOfRange: Int = 0) = VitalSignSummary(
        type = type,
        averageValue = average,
        minValue = min,
        maxValue = max,
        readingsCount = 2016,
        outOfRangeCount = outOfRange,
        stability = when {
            outOfRange == 0 -> StabilityIndex.STABLE
            outOfRange > 3 -> StabilityIndex.RECURRENT
            else -> StabilityIndex.UNSTABLE
        }
    )
    return HealthReport(
        id = id,
        careRecipientProfileId = "elena",
        reportType = reportType,
        periodStart = periodEnd.minusDays(6),
        periodEnd = periodEnd,
        summaries = listOf(
            summary(VitalSignType.HR, 76.3, 71.0, 84.0),
            summary(VitalSignType.BP_SYS, 118.3, 115.0, 146.0, outOfRange = 2),
            summary(VitalSignType.BP_DIA, 76.4, 72.0, 80.0),
            summary(VitalSignType.SPO2, 97.2, 95.0, 99.0),
            summary(VitalSignType.TEMP, 36.6, 36.2, 37.1),
            summary(VitalSignType.RESP_RATE, 16.1, 14.0, 19.0)
        ),
        recurrentAnomaliesCount = 0,
        clinicallyStable = false,
        generatedAt = Instant.parse("2026-10-09T19:32:00Z")
    )
}
