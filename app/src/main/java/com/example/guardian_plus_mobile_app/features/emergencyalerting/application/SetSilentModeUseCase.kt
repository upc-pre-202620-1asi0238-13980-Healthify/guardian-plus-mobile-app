package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettings
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettingsRepository
import javax.inject.Inject

/** Silent mode (US22): only critical alerts stay audible on the wristband. */
class SetSilentModeUseCase @Inject constructor(
    private val repository: AlertSettingsRepository
) {
    suspend operator fun invoke(careRecipientProfileId: String, enabled: Boolean): Result<AlertSettings> =
        repository.setSilentMode(careRecipientProfileId, enabled)
}
