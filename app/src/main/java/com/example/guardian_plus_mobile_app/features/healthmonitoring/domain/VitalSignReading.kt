package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain


import java.time.Instant

// One past reading. Unlike LiveVitalSign it carries no classification: the history endpoint does not send it
data class VitalSignReading(
    val id: String,
    val type: VitalSignType,
    val value: Double,
    val measuredAt: Instant
)
