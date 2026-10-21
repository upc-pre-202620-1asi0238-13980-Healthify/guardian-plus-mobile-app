package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.Instant

interface SleepCycleRecordRepository {
    /** Closed nights, most recent first, optionally only those that ended at or after [from]. */
    suspend fun getSleepCycleRecords(personUnderCareId: String, from: Instant? = null): Result<List<SleepCycleRecord>>
}
