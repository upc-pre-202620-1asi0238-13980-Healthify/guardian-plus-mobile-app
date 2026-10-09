package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

import java.time.Instant
import java.time.LocalDate

/** Summary of every vital sign over a period, compiled by the platform on demand or every week. */
data class HealthReport(
    val id: String,
    val careRecipientProfileId: String,
    val reportType: HealthReportType,
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val summaries: List<VitalSignSummary>,
    val recurrentAnomaliesCount: Int,
    // Every sign stayed within its normal range for the whole period
    val clinicallyStable: Boolean,
    val generatedAt: Instant
) {
    val readingsCount: Int get() = summaries.sumOf { it.readingsCount }

    val outOfRangeCount: Int get() = summaries.sumOf { it.outOfRangeCount }

    operator fun get(type: VitalSignType): VitalSignSummary? = summaries.firstOrNull { it.type == type }
}

enum class HealthReportType {
    ON_DEMAND,
    WEEKLY_AUTOMATIC
}
