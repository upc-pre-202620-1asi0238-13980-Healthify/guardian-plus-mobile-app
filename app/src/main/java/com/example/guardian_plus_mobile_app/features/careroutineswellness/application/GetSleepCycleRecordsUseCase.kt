package com.example.guardian_plus_mobile_app.features.careroutineswellness.application

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecord
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecordRepository
import javax.inject.Inject

class GetSleepCycleRecordsUseCase @Inject constructor(
    private val repository: SleepCycleRecordRepository
) {
    suspend operator fun invoke(personUnderCareId: String): Result<List<SleepCycleRecord>> =
        repository.getSleepCycleRecords(personUnderCareId)
}
