package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ScheduleReminderRequest
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.RecurrenceDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ReminderDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ScheduleReminderRequestDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.ReminderService
import java.time.Instant
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val service: ReminderService
) : ReminderRepository {

    override suspend fun getReminders(
        personUnderCareId: String,
        from: Instant?,
        to: Instant?,
        type: ReminderType?
    ): Result<List<Reminder>> = apiCall({
        service.getReminders(personUnderCareId, from?.toString(), to?.toString(), type?.name)
    }) { dtos -> dtos.map { it.toDomain() } }

    override suspend fun scheduleReminder(request: ScheduleReminderRequest): Result<Reminder> = apiCall({
        service.scheduleReminder(
            ScheduleReminderRequestDto(
                personUnderCareId = request.personUnderCareId,
                type = request.type.name,
                scheduledTime = request.scheduledTime.toString(),
                title = request.title.trim(),
                dosage = request.dosage?.trim()?.takeIf { it.isNotEmpty() },
                instructions = request.instructions?.trim()?.takeIf { it.isNotEmpty() },
                location = request.location?.trim()?.takeIf { it.isNotEmpty() },
                durationMinutes = request.durationMinutes,
                leadTimeMinutes = request.leadTimeMinutes,
                recurrence = RecurrenceDto(
                    frequency = request.frequency.name,
                    daysOfWeek = request.daysOfWeek.map { it.name }.takeIf { it.isNotEmpty() },
                    intervalHours = null
                )
            )
        )
    }) { it.toDomain() }

    override suspend fun confirmReminder(reminderId: String): Result<Reminder> =
        apiCall({ service.confirmReminder(reminderId) }) { it.toDomain() }

    override suspend fun markReminderAsMissed(reminderId: String): Result<Reminder> =
        apiCall({ service.markReminderAsMissed(reminderId) }) { it.toDomain() }

    override suspend fun cancelReminder(reminderId: String): Result<Reminder> =
        apiCall({ service.cancelReminder(reminderId) }) { it.toDomain() }
}

private fun ReminderDto.toDomain() = Reminder(
    id = id,
    personUnderCareId = personUnderCareId,
    type = ReminderType.valueOf(type),
    title = title,
    dosage = dosage,
    instructions = instructions,
    location = location,
    durationMinutes = durationMinutes,
    scheduledTime = Instant.parse(scheduledTime),
    leadTimeMinutes = leadTimeMinutes ?: 0,
    confirmedAt = confirmedAt?.let(Instant::parse),
    status = ReminderStatus.valueOf(status)
)
