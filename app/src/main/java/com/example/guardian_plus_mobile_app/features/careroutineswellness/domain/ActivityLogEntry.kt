package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.Instant

/** A noteworthy activity fact: movement after a still period, a finished walk or an inactivity detection. */
data class ActivityLogEntry(
    val id: String,
    val type: ActivityLogEntryType,
    // Start of the walk, for walks
    val occurredAt: Instant,
    val durationMinutes: Int?,
    val steps: Int?,
    // Minutes the person had been still, for movement and inactivity entries
    val inactiveMinutes: Double?
)

enum class ActivityLogEntryType {
    MOVEMENT_DETECTED,
    WALK_DETECTED,
    PROLONGED_INACTIVITY_DETECTED
}
