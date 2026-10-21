package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

interface ActivityMonitorRepository {
    /** Null until the wristband sends its first activity sample or the family configures detection. */
    suspend fun getActivityMonitor(personUnderCareId: String): Result<ActivityMonitor?>

    /** Turns prolonged-inactivity detection on or off and sets its threshold (30 to 240 minutes). */
    suspend fun configureInactivityDetection(
        personUnderCareId: String,
        enabled: Boolean,
        thresholdMinutes: Int
    ): Result<ActivityMonitor>

    /** Most recent noteworthy activity, newest first. */
    suspend fun getActivityLog(personUnderCareId: String, limit: Int): Result<List<ActivityLogEntry>>
}
