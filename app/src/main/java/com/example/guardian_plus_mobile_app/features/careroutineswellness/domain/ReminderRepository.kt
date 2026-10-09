package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.Instant

interface ReminderRepository {
    /** Reminders ordered by scheduled time, optionally narrowed to [from, to) and to one [type]. */
    suspend fun getReminders(
        personUnderCareId: String,
        from: Instant? = null,
        to: Instant? = null,
        type: ReminderType? = null
    ): Result<List<Reminder>>
}
