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
    val liveSignal: Boolean
)
