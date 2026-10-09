package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.simulated

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepClassification
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecord
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecordRepository
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/** Last night of the prototype (23:12 to 06:36), served locally until the app reads /sleep-cycle-records. */
class SimulatedSleepCycleRecordRepository @Inject constructor() : SleepCycleRecordRepository {

    override suspend fun getSleepCycleRecords(personUnderCareId: String): Result<List<SleepCycleRecord>> {
        val zone = ZoneId.systemDefault()
        // Before 06:36 the night in progress is not closed yet, so the last one is the night before
        val now = LocalDateTime.now(zone)
        val wakeUpDay = if (now.toLocalTime().isBefore(WAKE_UP)) now.toLocalDate().minusDays(1) else now.toLocalDate()
        val start = wakeUpDay.minusDays(1).atTime(BEDTIME).atZone(zone).toInstant()
        val end = wakeUpDay.atTime(WAKE_UP).atZone(zone).toInstant()
        return Result.success(
            listOf(
                SleepCycleRecord(
                    id = "sleep-last-night",
                    startTime = start,
                    endTime = end,
                    durationMinutes = Duration.between(start, end).toMinutes(),
                    interruptionCount = 5,
                    classification = SleepClassification.FRAGMENTED
                )
            )
        )
    }

    private companion object {
        val BEDTIME: LocalTime = LocalTime.of(23, 12)
        val WAKE_UP: LocalTime = LocalTime.of(6, 36)
    }
}
