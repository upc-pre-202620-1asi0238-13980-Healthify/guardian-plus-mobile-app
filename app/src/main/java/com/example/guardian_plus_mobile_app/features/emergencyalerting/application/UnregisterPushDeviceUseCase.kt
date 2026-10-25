package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.PushTokenProvider
import javax.inject.Inject

/**
 * Stops push alerts on this phone after signing out. The session is already gone, so instead of asking the
 * platform the token itself is invalidated: deliveries to it fail and the next account registers a new one.
 */
class UnregisterPushDeviceUseCase @Inject constructor(
    private val pushTokenProvider: PushTokenProvider
) {
    suspend operator fun invoke(): Result<Unit> = pushTokenProvider.deleteToken()
}
