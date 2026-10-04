package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettings
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettingsRepository
import javax.inject.Inject

/** Silent mode (US22): the Care Circle gets non-critical alerts of this person without sound; critical ones always sound. */
class SetSilentModeUseCase @Inject constructor(
    private val repository: AlertSettingsRepository
) {
    suspend operator fun invoke(careRecipientProfileId: String, enabled: Boolean): Result<AlertSettings> =
        repository.setSilentMode(careRecipientProfileId, enabled)
}
