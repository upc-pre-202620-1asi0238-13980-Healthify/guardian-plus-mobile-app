package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto

// Same shape as the platform's ReminderResource; instants as ISO-8601 strings
data class ReminderDto(
    val id: String,
    val seriesId: String?,
    val personUnderCareId: String,
    val type: String,
    val title: String?,
    val dosage: String?,
    val instructions: String?,
    val location: String?,
    val durationMinutes: Int?,
    val scheduledTime: String,
    val leadTimeMinutes: Int?,
    val notifyAt: String?,
    val recurrence: RecurrenceDto?,
    val medicationStockId: String?,
    val issuedAt: String?,
    val confirmedAt: String?,
    val status: String,
    val reissueCount: Int?
)

data class RecurrenceDto(
    val frequency: String,
    val daysOfWeek: List<String>?,
    val intervalHours: Int?
)

// Same body as the platform's ScheduleReminderResource
data class ScheduleReminderRequestDto(
    val personUnderCareId: String,
    val type: String,
    val scheduledTime: String,
    val title: String,
    val dosage: String?,
    val instructions: String?,
    val location: String?,
    val durationMinutes: Int?,
    val leadTimeMinutes: Int,
    val recurrence: RecurrenceDto
)
