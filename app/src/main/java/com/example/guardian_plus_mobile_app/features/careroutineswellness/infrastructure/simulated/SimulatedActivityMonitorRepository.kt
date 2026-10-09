package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.simulated

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitor
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitorRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityStatus
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import javax.inject.Inject

/** Daytime inactivity watch of the prototype, served locally until the app reads /activity-monitors. */
class SimulatedActivityMonitorRepository @Inject constructor() : ActivityMonitorRepository {

    override suspend fun getActivityMonitor(personUnderCareId: String): Result<ActivityMonitor?> {
        val now = Instant.now()
        return Result.success(
            ActivityMonitor(
                status = ActivityStatus.NORMAL,
                inactivitySince = null,
                lastMovementAt = now - Duration.ofMinutes(3),
                lastSampleAt = now - Duration.ofMinutes(1),
                detectionEnabled = true,
                thresholdMinutes = 60,
                watchHoursStart = LocalTime.of(7, 0),
                watchHoursEnd = LocalTime.of(22, 0)
            )
        )
    }
}
