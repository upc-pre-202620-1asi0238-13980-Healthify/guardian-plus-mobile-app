package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.LocalDate

/** How many of the reminders of one type that reached the person were confirmed in a period. */
data class Adherence(
    val type: ReminderType,
    val from: LocalDate,
    val to: LocalDate,
    val due: Int,
    val confirmed: Int,
    val percentage: Int
) {
    // 0 % with nothing due means "no doses scheduled", not "none taken"
    val hasDueReminders: Boolean get() = due > 0
}
