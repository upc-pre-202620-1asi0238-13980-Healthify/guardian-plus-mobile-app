package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.Instant
import java.time.LocalTime

/** Daytime watch for prolonged inactivity and the last movement the wristband reported. */
data class ActivityMonitor(
    val status: ActivityStatus,
    val inactivitySince: Instant?,
    // Current minutes without movement, e.g. 3.3 for "03:18"
    val inactiveMinutes: Double,
    val lastMovementAt: Instant?,
    // Last telemetry sample received, i.e. when the wristband last synced
    val lastSampleAt: Instant?,
    val detectionEnabled: Boolean,
    val thresholdMinutes: Int,
    // Inactivity is only watched outside the sleep window, e.g. 07:00 to 22:00
    val watchHoursStart: LocalTime?,
    val watchHoursEnd: LocalTime?
)
