package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

interface AlertSettingsRepository {
    suspend fun getAlertSettings(careRecipientProfileId: String): Result<AlertSettings>
    suspend fun updateAlertSettings(
        careRecipientProfileId: String,
        primaryAckTimeoutSec: Int,
        escalationEnabled: Boolean,
        broadcastCriticalImmediately: Boolean
    ): Result<AlertSettings>
    suspend fun setSilentMode(careRecipientProfileId: String, enabled: Boolean): Result<AlertSettings>
}
