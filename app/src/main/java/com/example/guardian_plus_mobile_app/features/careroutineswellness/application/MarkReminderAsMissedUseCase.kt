package com.example.guardian_plus_mobile_app.features.careroutineswellness.application

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderRepository
import javax.inject.Inject

class MarkReminderAsMissedUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(reminderId: String): Result<Reminder> = repository.markReminderAsMissed(reminderId)
}
