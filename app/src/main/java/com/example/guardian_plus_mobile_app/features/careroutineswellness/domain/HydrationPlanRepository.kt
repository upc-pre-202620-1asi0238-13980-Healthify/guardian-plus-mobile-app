package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

interface HydrationPlanRepository {
    /** Null while the family has not configured a plan yet. */
    suspend fun getHydrationPlan(personUnderCareId: String): Result<HydrationPlan?>

    /** Creates or changes the plan; switching it or changing the interval replaces the pending reminders. */
    suspend fun configureHydrationPlan(
        personUnderCareId: String,
        active: Boolean,
        dailyGoalGlasses: Int,
        intervalHours: Int,
        respectSleepWindow: Boolean
    ): Result<HydrationPlan>
}
