package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityLogEntry
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityLogEntryType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitor
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitorRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ActivityMonitorDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ConfigureInactivityDetectionRequestDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.ActivityMonitorService
import java.time.Instant
import java.time.LocalTime
import javax.inject.Inject

class ActivityMonitorRepositoryImpl @Inject constructor(
    private val service: ActivityMonitorService
) : ActivityMonitorRepository {

    // 404 is the platform's "no telemetry or configuration received yet"
    override suspend fun getActivityMonitor(personUnderCareId: String): Result<ActivityMonitor?> =
        apiCall({ service.getActivityMonitor(personUnderCareId) }, onNotFound = { null }) { it.toDomain() }

    override suspend fun configureInactivityDetection(
        personUnderCareId: String,
        enabled: Boolean,
        thresholdMinutes: Int
    ): Result<ActivityMonitor> = apiCall({
        service.configureInactivityDetection(personUnderCareId, ConfigureInactivityDetectionRequestDto(enabled, thresholdMinutes))
    }) { it.toDomain() }

    override suspend fun getActivityLog(personUnderCareId: String, limit: Int): Result<List<ActivityLogEntry>> =
        apiCall({ service.getActivityLog(personUnderCareId, limit) }) { dtos ->
            // An entry type the app does not know yet is left out rather than failing the whole log
            dtos.mapNotNull { dto ->
                val type = ActivityLogEntryType.entries.firstOrNull { it.name == dto.type } ?: return@mapNotNull null
                ActivityLogEntry(
                    id = dto.id,
                    type = type,
                    occurredAt = Instant.parse(dto.occurredAt),
                    durationMinutes = dto.durationMinutes,
                    steps = dto.steps,
                    inactiveMinutes = dto.inactiveMinutes
                )
            }
        }
}

// Detection settings exist before the first sample, so the threshold falls back to the platform default
private const val DEFAULT_THRESHOLD_MINUTES = 60

private fun ActivityMonitorDto.toDomain() = ActivityMonitor(
    status = ActivityStatus.valueOf(status),
    inactivitySince = inactivitySince?.let(Instant::parse),
    inactiveMinutes = inactiveMinutes ?: 0.0,
    lastMovementAt = lastMovementAt?.let(Instant::parse),
    lastSampleAt = lastSampleAt?.let(Instant::parse),
    detectionEnabled = detectionEnabled,
    thresholdMinutes = thresholdMinutes ?: DEFAULT_THRESHOLD_MINUTES,
    watchHoursStart = watchHoursStart?.let(LocalTime::parse),
    watchHoursEnd = watchHoursEnd?.let(LocalTime::parse)
)
