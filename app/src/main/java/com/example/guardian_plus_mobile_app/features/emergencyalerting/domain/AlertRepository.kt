package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

import java.time.Instant

interface AlertRepository {
    /** [severity], [from] and [to] are optional filters; null means no filter. */
    suspend fun getAlertHistory(
        careRecipientProfileId: String,
        severity: Severity?,
        from: Instant?,
        to: Instant?,
        page: Int,
        size: Int
    ): Result<AlertPage>
    suspend fun getActiveAlerts(careRecipientProfileId: String): Result<List<Alert>>
    suspend fun getPendingAlerts(userId: String): Result<List<Alert>>
    suspend fun getAlertById(alertId: String): Result<Alert>
    suspend fun acknowledgeAlert(alertId: String, userId: String): Result<Alert>
    suspend fun claimAlertResponse(alertId: String, responderUserId: String): Result<Alert>
    suspend fun completeAlertResponse(alertId: String, responseId: String, notes: String?): Result<Alert>
}
