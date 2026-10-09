package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

interface ActivityMonitorRepository {
    /** Null until the wristband sends its first activity sample. */
    suspend fun getActivityMonitor(personUnderCareId: String): Result<ActivityMonitor?>
}
