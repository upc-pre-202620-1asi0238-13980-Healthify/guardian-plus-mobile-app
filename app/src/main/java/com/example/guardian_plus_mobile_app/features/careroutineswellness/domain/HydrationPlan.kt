package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.Instant

/** Daily water goal and reminder interval, with today's progress. */
data class HydrationPlan(
    val active: Boolean,
    val dailyGoalGlasses: Int,
    val intervalHours: Int,
    val respectSleepWindow: Boolean,
    val glassesConsumedToday: Int,
    val remainingGlassesToday: Int,
    val nextReminderAt: Instant?
)
