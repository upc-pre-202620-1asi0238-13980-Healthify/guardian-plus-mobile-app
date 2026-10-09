package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

interface HydrationPlanRepository {
    /** Null while the family has not configured a plan yet. */
    suspend fun getHydrationPlan(personUnderCareId: String): Result<HydrationPlan?>
}
