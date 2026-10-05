package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

import java.time.Instant

data class LiveVitalSign(
    val id: String,
    val type: VitalSignType,
    val typeName: String,
    val unit: String,
    val value: Double,
    val measuredAt: Instant,
    val normalMinimum: Double,
    val normalMaximum: Double,
    val classification: ReadingClassification,
    // false when the reading is older than the live window (60s): It's the last known value, not a live one
    val liveSignal: Boolean
)
