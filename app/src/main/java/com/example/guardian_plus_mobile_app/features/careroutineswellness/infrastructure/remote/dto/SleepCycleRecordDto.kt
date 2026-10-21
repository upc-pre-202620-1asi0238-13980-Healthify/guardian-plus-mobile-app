package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto

// Same shape as the platform's SleepCycleRecordResource
data class SleepCycleRecordDto(
    val id: String,
    val personUnderCareId: String,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Long,
    val interruptionCount: Int,
    val continuityScore: Int,
    val classification: String
)
