package com.example.guardian_plus_mobile_app.features.careroutineswellness.application

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ScheduleReminderRequest
import javax.inject.Inject

class ScheduleReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(request: ScheduleReminderRequest): Result<Reminder> = repository.scheduleReminder(request)
}
