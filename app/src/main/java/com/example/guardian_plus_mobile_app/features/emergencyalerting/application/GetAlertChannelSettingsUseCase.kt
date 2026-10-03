package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSetting
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSettingRepository
import javax.inject.Inject

class GetAlertChannelSettingsUseCase @Inject constructor(
    private val repository: AlertChannelSettingRepository
) {
    suspend operator fun invoke(userId: String): Result<List<AlertChannelSetting>> =
        repository.getChannelSettings(userId)
}
