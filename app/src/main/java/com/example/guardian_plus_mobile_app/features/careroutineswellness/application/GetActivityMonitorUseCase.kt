package com.example.guardian_plus_mobile_app.features.careroutineswellness.application

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitor
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitorRepository
import javax.inject.Inject

class GetActivityMonitorUseCase @Inject constructor(
    private val repository: ActivityMonitorRepository
) {
    suspend operator fun invoke(personUnderCareId: String): Result<ActivityMonitor?> =
        repository.getActivityMonitor(personUnderCareId)
}
