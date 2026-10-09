package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto

data class HealthReportDto(
    val id: String,
    val careRecipientProfileId: String,
    val reportType: String,
    val periodStart: String,
    val periodEnd: String,
    val summaries: List<VitalSignSummaryDto>,
    val recurrentAnomaliesCount: Int,
    val clinicallyStable: Boolean,
    val generatedAt: String
)

data class VitalSignSummaryDto(
    val metricType: String,
    val averageValue: Double,
    val minValue: Double,
    val maxValue: Double,
    val readingsCount: Int,
    val outOfRangeCount: Int,
    val stabilityIndex: String
)

// Same body as the platform's GenerateHealthReportResource; dates as yyyy-MM-dd
data class GenerateHealthReportRequestDto(
    val careRecipientProfileId: String,
    val generatedByUserId: String,
    val periodStart: String,
    val periodEnd: String
)
