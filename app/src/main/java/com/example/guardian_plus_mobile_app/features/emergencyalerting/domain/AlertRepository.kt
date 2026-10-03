package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

interface AlertRepository {
    suspend fun getActiveAlerts(careRecipientProfileId: String): Result<List<Alert>>
    suspend fun getPendingAlerts(userId: String): Result<List<Alert>>
    suspend fun getAlertById(alertId: String): Result<Alert>
    suspend fun acknowledgeAlert(alertId: String, userId: String): Result<Alert>
    suspend fun claimAlertResponse(alertId: String, responderUserId: String): Result<Alert>
    suspend fun completeAlertResponse(alertId: String, responseId: String, notes: String?): Result<Alert>
}
