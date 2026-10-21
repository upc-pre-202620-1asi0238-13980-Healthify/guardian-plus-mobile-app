package com.example.guardian_plus_mobile_app.features.careroutineswellness.application

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityLogEntry
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitorRepository
import javax.inject.Inject

class GetActivityLogUseCase @Inject constructor(
    private val repository: ActivityMonitorRepository
) {
    suspend operator fun invoke(personUnderCareId: String, limit: Int): Result<List<ActivityLogEntry>> =
        repository.getActivityLog(personUnderCareId, limit)
}
