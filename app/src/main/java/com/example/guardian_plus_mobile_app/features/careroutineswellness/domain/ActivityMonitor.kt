package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.Instant
import java.time.LocalTime

/** Daytime watch for prolonged inactivity and the last movement the wristband reported. */
data class ActivityMonitor(
    val status: ActivityStatus,
    val inactivitySince: Instant?,
    val lastMovementAt: Instant?,
    // Last telemetry sample received, i.e. when the wristband last synced
    val lastSampleAt: Instant?,
    val detectionEnabled: Boolean,
    val thresholdMinutes: Int,
    val watchHoursStart: LocalTime,
    val watchHoursEnd: LocalTime
)
