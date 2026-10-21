package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto

// Same shape as the platform's ActivityMonitorResource
data class ActivityMonitorDto(
    val id: String,
    val personUnderCareId: String,
    val status: String,
    val inactivitySince: String?,
    val inactiveMinutes: Double?,
    val lastMovementAt: String?,
    val lastSampleAt: String?,
    val detectionEnabled: Boolean,
    val thresholdMinutes: Int?,
    val watchHoursStart: String?,
    val watchHoursEnd: String?
)

// Same body as the platform's ConfigureInactivityDetectionResource
data class ConfigureInactivityDetectionRequestDto(
    val enabled: Boolean,
    val thresholdMinutes: Int
)

// Same shape as the platform's ActivityLogEntryResource
data class ActivityLogEntryDto(
    val id: String,
    val type: String,
    val occurredAt: String,
    val durationMinutes: Int?,
    val steps: Int?,
    val inactiveMinutes: Double?
)
