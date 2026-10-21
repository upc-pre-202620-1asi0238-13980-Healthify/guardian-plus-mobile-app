package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.Instant

/** One occurrence of a routine: a dose, an appointment, an activity block or a glass of water. */
data class Reminder(
    val id: String,
    val personUnderCareId: String,
    val type: ReminderType,
    // "Losartán", "Control de cardiología"; mandatory on the platform, kept optional for older records
    val title: String?,
    val dosage: String? = null,
    val instructions: String? = null,
    val location: String? = null,
    val durationMinutes: Int? = null,
    val scheduledTime: Instant,
    // How long before scheduledTime the reminder reaches the person, e.g. 60 for "1 hora antes"
    val leadTimeMinutes: Int = 0,
    val confirmedAt: Instant? = null,
    val status: ReminderStatus
) {
    /** Issued (or reissued) and waiting for the person; the only states the platform lets be confirmed. */
    val canBeConfirmed: Boolean get() = status == ReminderStatus.ISSUED || status == ReminderStatus.REISSUED
}
