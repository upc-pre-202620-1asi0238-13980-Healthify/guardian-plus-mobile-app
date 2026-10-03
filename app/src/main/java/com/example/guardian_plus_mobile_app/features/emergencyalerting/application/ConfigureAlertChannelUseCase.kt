package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSetting
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSettingRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.NotificationChannel
import javax.inject.Inject

/** Turns one channel on or off (US12). The platform refuses to turn off the member's last channel. */
class ConfigureAlertChannelUseCase @Inject constructor(
    private val repository: AlertChannelSettingRepository
) {
    suspend operator fun invoke(userId: String, channel: NotificationChannel, enabled: Boolean): Result<AlertChannelSetting> =
        repository.configureChannel(userId, channel, enabled)
}
