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

    suspend fun scheduleReminder(request: ScheduleReminderRequest): Result<Reminder>

    /** Only an issued or reissued reminder can be confirmed. */
    suspend fun confirmReminder(reminderId: String): Result<Reminder>

    /** Closes an issued or reissued reminder the person never confirmed, once the family reviewed it. */
    suspend fun markReminderAsMissed(reminderId: String): Result<Reminder>

    /** Cancelling the pending occurrence of a recurring reminder stops its series. */
    suspend fun cancelReminder(reminderId: String): Result<Reminder>
}
