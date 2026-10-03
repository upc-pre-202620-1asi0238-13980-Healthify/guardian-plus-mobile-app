package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

interface AlertSettingsRepository {
    suspend fun getAlertSettings(careRecipientProfileId: String): Result<AlertSettings>
}
