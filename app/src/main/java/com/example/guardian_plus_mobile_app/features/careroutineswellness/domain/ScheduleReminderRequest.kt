package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.DayOfWeek
import java.time.Instant

/** What the family enters to schedule a routine. */
data class ScheduleReminderRequest(
    val personUnderCareId: String,
    val type: ReminderType,
    val scheduledTime: Instant,
    val title: String,
    val dosage: String? = null,
    val instructions: String? = null,
    val location: String? = null,
    val durationMinutes: Int? = null,
    val leadTimeMinutes: Int = 0,
    val frequency: RecurrenceFrequency = RecurrenceFrequency.ONCE,
    // Only for WEEKLY, where the platform requires at least one day
    val daysOfWeek: Set<DayOfWeek> = emptySet()
)
