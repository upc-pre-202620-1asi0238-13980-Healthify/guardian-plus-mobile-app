package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.Instant

/** A closed night of sleep measured by the wristband. */
data class SleepCycleRecord(
    val id: String,
    val startTime: Instant,
    val endTime: Instant,
    val durationMinutes: Long,
    val interruptionCount: Int,
    val classification: SleepClassification
)
