package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlan
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlanRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ConfigureHydrationPlanRequestDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.HydrationPlanDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.HydrationPlanService
import java.time.Instant
import java.time.LocalTime
import javax.inject.Inject

class HydrationPlanRepositoryImpl @Inject constructor(
    private val service: HydrationPlanService
) : HydrationPlanRepository {

    // 404 is the platform's "no plan configured yet"
    override suspend fun getHydrationPlan(personUnderCareId: String): Result<HydrationPlan?> =
        apiCall({ service.getHydrationPlan(personUnderCareId) }, onNotFound = { null }) { it.toDomain() }

    override suspend fun configureHydrationPlan(
        personUnderCareId: String,
        active: Boolean,
        dailyGoalGlasses: Int,
        intervalHours: Int,
        respectSleepWindow: Boolean
    ): Result<HydrationPlan> = apiCall({
        service.configureHydrationPlan(
            personUnderCareId,
            ConfigureHydrationPlanRequestDto(active, dailyGoalGlasses, intervalHours, respectSleepWindow)
        )
    }) { it.toDomain() }
}

private fun HydrationPlanDto.toDomain() = HydrationPlan(
    active = active,
    dailyGoalGlasses = dailyGoalGlasses,
    intervalHours = intervalHours,
    respectSleepWindow = respectSleepWindow,
    sleepWindowStart = sleepWindowStart?.let(LocalTime::parse),
    sleepWindowEnd = sleepWindowEnd?.let(LocalTime::parse),
    glassesConsumedToday = glassesConsumedToday,
    remainingGlassesToday = remainingGlassesToday,
    nextReminderAt = nextReminderAt?.let(Instant::parse)
)
