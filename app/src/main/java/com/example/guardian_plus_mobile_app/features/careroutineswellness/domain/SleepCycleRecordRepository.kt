package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

interface SleepCycleRecordRepository {
    /** Closed nights, most recent first. */
    suspend fun getSleepCycleRecords(personUnderCareId: String): Result<List<SleepCycleRecord>>
}
