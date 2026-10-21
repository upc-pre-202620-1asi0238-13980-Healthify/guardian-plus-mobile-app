package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto

// Same shape as the platform's HydrationPlanResource; times of day as "22:00" or "22:00:00"
data class HydrationPlanDto(
    val id: String,
    val personUnderCareId: String,
    val active: Boolean,
    val dailyGoalGlasses: Int,
    val intervalHours: Int,
    val respectSleepWindow: Boolean,
    val sleepWindowStart: String?,
    val sleepWindowEnd: String?,
    val glassesConsumedToday: Int,
    val remainingGlassesToday: Int,
    val nextReminderAt: String?
)

// Same body as the platform's ConfigureHydrationPlanResource
data class ConfigureHydrationPlanRequestDto(
    val active: Boolean,
    val dailyGoalGlasses: Int,
    val intervalHours: Int,
    val respectSleepWindow: Boolean
)
