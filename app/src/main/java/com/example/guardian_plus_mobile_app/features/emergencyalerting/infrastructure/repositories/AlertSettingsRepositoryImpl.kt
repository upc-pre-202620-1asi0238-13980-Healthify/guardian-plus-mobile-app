package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettings
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettingsRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.AlertSettingsService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.UpdateAlertSettingsRequestDto
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.apiCall
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.toDomain
import javax.inject.Inject

class AlertSettingsRepositoryImpl @Inject constructor(
    private val service: AlertSettingsService
) : AlertSettingsRepository {

    override suspend fun getAlertSettings(careRecipientProfileId: String): Result<AlertSettings> =
        apiCall({ service.getAlertSettings(careRecipientProfileId) }) { dto -> dto.toDomain() }

    override suspend fun updateAlertSettings(
        careRecipientProfileId: String,
        primaryAckTimeoutSec: Int,
        escalationEnabled: Boolean,
        broadcastCriticalImmediately: Boolean
    ): Result<AlertSettings> = apiCall({
        service.updateAlertSettings(
            careRecipientProfileId,
            UpdateAlertSettingsRequestDto(primaryAckTimeoutSec, escalationEnabled, broadcastCriticalImmediately)
        )
    }) { dto -> dto.toDomain() }

    // The platform models silent mode as a resource that is created (on) or deleted (off)
    override suspend fun setSilentMode(careRecipientProfileId: String, enabled: Boolean): Result<AlertSettings> =
        apiCall({
            if (enabled) service.activateSilentMode(careRecipientProfileId) else service.deactivateSilentMode(careRecipientProfileId)
        }) { dto -> dto.toDomain() }
}
