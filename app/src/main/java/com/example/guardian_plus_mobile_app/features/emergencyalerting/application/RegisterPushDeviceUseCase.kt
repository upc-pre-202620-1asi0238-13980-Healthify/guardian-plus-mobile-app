package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSetting
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSettingRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.PushTokenProvider
import javax.inject.Inject

/**
 * Registers this phone for push alerts (US08, US15): reads its FCM token, or uses the one the provider just
 * rotated, and turns PUSH on with it for the signed-in member.
 */
class RegisterPushDeviceUseCase @Inject constructor(
    private val pushTokenProvider: PushTokenProvider,
    private val repository: AlertChannelSettingRepository
) {
    suspend operator fun invoke(userId: String, deviceToken: String? = null): Result<AlertChannelSetting> {
        val token = deviceToken ?: pushTokenProvider.currentToken().getOrElse { return Result.failure(it) }
        return repository.registerPushDevice(userId, token)
    }
}
