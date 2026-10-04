package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettings
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettingsRepository
import javax.inject.Inject

class UpdateAlertSettingsUseCase @Inject constructor(
    private val repository: AlertSettingsRepository
) {
    suspend operator fun invoke(
        careRecipientProfileId: String,
        primaryAckTimeoutSec: Int,
        escalationEnabled: Boolean,
        broadcastCriticalImmediately: Boolean
    ): Result<AlertSettings> = repository.updateAlertSettings(
        careRecipientProfileId,
        primaryAckTimeoutSec,
        escalationEnabled,
        broadcastCriticalImmediately
    )
}
