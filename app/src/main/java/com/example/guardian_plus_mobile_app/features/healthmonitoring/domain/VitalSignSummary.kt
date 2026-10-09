package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

/** Average, extremes and stability of one vital sign within a health report. */
data class VitalSignSummary(
    val type: VitalSignType,
    val averageValue: Double,
    val minValue: Double,
    val maxValue: Double,
    val readingsCount: Int,
    val outOfRangeCount: Int,
    val stability: StabilityIndex
)

/** STABLE with no reading out of range, RECURRENT past three of them, UNSTABLE in between (platform rule). */
enum class StabilityIndex {
    STABLE,
    UNSTABLE,
    RECURRENT
}
