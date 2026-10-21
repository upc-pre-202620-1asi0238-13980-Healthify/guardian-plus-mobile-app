package com.example.guardian_plus_mobile_app.features.careroutineswellness.application

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlan
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlanRepository
import javax.inject.Inject

class ConfigureHydrationPlanUseCase @Inject constructor(
    private val repository: HydrationPlanRepository
) {
    suspend operator fun invoke(
        personUnderCareId: String,
        active: Boolean,
        dailyGoalGlasses: Int,
        intervalHours: Int,
        respectSleepWindow: Boolean
    ): Result<HydrationPlan> =
        repository.configureHydrationPlan(personUnderCareId, active, dailyGoalGlasses, intervalHours, respectSleepWindow)
}
