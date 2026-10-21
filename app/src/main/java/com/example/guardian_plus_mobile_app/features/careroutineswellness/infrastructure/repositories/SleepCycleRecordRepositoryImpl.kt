package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepClassification
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecord
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecordRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.SleepCycleRecordService
import java.time.Instant
import javax.inject.Inject

class SleepCycleRecordRepositoryImpl @Inject constructor(
    private val service: SleepCycleRecordService
) : SleepCycleRecordRepository {

    override suspend fun getSleepCycleRecords(personUnderCareId: String, from: Instant?): Result<List<SleepCycleRecord>> =
        apiCall({ service.getSleepCycleRecords(personUnderCareId, from?.toString()) }) { dtos ->
            dtos.map { dto ->
                SleepCycleRecord(
                    id = dto.id,
                    startTime = Instant.parse(dto.startTime),
                    endTime = Instant.parse(dto.endTime),
                    durationMinutes = dto.durationMinutes,
                    interruptionCount = dto.interruptionCount,
                    continuityScore = dto.continuityScore,
                    classification = SleepClassification.valueOf(dto.classification)
                )
            }.sortedByDescending { it.endTime }
        }
}
