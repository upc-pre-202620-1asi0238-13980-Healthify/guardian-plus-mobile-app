package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.Instant

/** One occurrence of a routine: a dose, an appointment, an activity block or a glass of water. */
data class Reminder(
    val id: String,
    val personUnderCareId: String,
    val type: ReminderType,
    // "Losartán 50 mg", "Control de cardiología"; optional on the platform
    val title: String?,
    val dosage: String? = null,
    val instructions: String? = null,
    val location: String? = null,
    val durationMinutes: Int? = null,
    val scheduledTime: Instant,
    val status: ReminderStatus
)
