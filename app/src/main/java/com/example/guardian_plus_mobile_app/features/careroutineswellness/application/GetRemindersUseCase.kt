package com.example.guardian_plus_mobile_app.features.careroutineswellness.application

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import java.time.Instant
import javax.inject.Inject

class GetRemindersUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(
        personUnderCareId: String,
        from: Instant? = null,
        to: Instant? = null,
        type: ReminderType? = null
    ): Result<List<Reminder>> = repository.getReminders(personUnderCareId, from, to, type)
}
