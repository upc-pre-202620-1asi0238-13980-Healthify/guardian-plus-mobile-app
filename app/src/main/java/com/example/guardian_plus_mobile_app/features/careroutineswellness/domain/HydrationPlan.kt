package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.Instant
import java.time.LocalTime

/** Daily water goal and reminder interval, with today's progress. */
data class HydrationPlan(
    val active: Boolean,
    val dailyGoalGlasses: Int,
    val intervalHours: Int,
    val respectSleepWindow: Boolean,
    // Hours without hydration reminders while respectSleepWindow is on, e.g. 22:00 to 07:00
    val sleepWindowStart: LocalTime?,
    val sleepWindowEnd: LocalTime?,
    val glassesConsumedToday: Int,
    val remainingGlassesToday: Int,
    val nextReminderAt: Instant?
) {
    val goalReached: Boolean get() = glassesConsumedToday >= dailyGoalGlasses

    val progressPercentage: Int
        get() = if (dailyGoalGlasses == 0) 0 else (glassesConsumedToday * 100 / dailyGoalGlasses).coerceAtMost(100)
}
