package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.simulated

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlan
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlanRepository
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

/** Sample hydration plan, served locally until the app reads /hydration-plans from the platform. */
class SimulatedHydrationPlanRepository @Inject constructor() : HydrationPlanRepository {

    override suspend fun getHydrationPlan(personUnderCareId: String): Result<HydrationPlan?> = Result.success(
        HydrationPlan(
            active = true,
            dailyGoalGlasses = 6,
            intervalHours = 2,
            respectSleepWindow = true,
            glassesConsumedToday = 4,
            remainingGlassesToday = 2,
            nextReminderAt = Instant.now() + Duration.ofMinutes(70)
        )
    )
}
