package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.Instant

/** A closed night of sleep measured by the wristband. */
data class SleepCycleRecord(
    val id: String,
    val startTime: Instant,
    val endTime: Instant,
    val durationMinutes: Long,
    val interruptionCount: Int,
    // How continuous the night was, from 0 to 100
    val continuityScore: Int,
    val classification: SleepClassification
)
