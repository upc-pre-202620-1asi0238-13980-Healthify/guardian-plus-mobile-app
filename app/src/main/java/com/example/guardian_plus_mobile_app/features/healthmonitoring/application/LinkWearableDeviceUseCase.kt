package com.example.guardian_plus_mobile_app.features.healthmonitoring.application

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.DeviceType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.WearableDeviceRepository
import javax.inject.Inject

/**
 * Links the wearable a care recipient will wear, so the platform accepts its readings. The serial number is the
 * one printed on the device (the IoT simulator uses the same ones).
 */
class LinkWearableDeviceUseCase @Inject constructor(
    private val repository: WearableDeviceRepository
) {
    suspend operator fun invoke(
        careRecipientProfileId: String,
        serialNumber: String,
        deviceType: DeviceType
    ): Result<WearableDevice> = repository.linkWearableDevice(careRecipientProfileId, serialNumber.trim(), deviceType)
}
