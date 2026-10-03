package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettings
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettingsRepository
import javax.inject.Inject

class GetAlertSettingsUseCase @Inject constructor(
    private val repository: AlertSettingsRepository
) {
    suspend operator fun invoke(careRecipientProfileId: String): Result<AlertSettings> =
        repository.getAlertSettings(careRecipientProfileId)
}
