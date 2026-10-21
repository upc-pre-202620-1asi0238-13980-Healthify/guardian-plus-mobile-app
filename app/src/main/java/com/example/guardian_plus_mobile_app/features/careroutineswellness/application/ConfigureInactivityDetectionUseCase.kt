package com.example.guardian_plus_mobile_app.features.careroutineswellness.application

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitor
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitorRepository
import javax.inject.Inject

class ConfigureInactivityDetectionUseCase @Inject constructor(
    private val repository: ActivityMonitorRepository
) {
    suspend operator fun invoke(personUnderCareId: String, enabled: Boolean, thresholdMinutes: Int): Result<ActivityMonitor> =
        repository.configureInactivityDetection(personUnderCareId, enabled, thresholdMinutes)
}
