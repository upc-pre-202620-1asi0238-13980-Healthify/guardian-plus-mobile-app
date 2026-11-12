package com.example.guardian_plus_mobile_app.features.healthmonitoring.application

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.WearableDeviceRepository
import javax.inject.Inject

/**
 * Unlinks the care recipient's wearable, so the platform stops accepting its readings. The readings it already
 * sent stay in the history and the same serial number can be linked again.
 */
class UnlinkWearableDeviceUseCase @Inject constructor(
    private val repository: WearableDeviceRepository
) {
    suspend operator fun invoke(wearableDeviceId: String): Result<WearableDevice> =
        repository.unlinkWearableDevice(wearableDeviceId)
}
