package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.DeviceType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.WearableDeviceRepository
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.services.WearableDeviceService
import java.time.Instant
import javax.inject.Inject


class WearableDeviceRepositoryImpl @Inject constructor(
    private val service: WearableDeviceService
) : WearableDeviceRepository{
    override suspend fun getWearableDevices(careRecipientProfileId: String): Result<List<WearableDevice>> = apiCall({
        service.getWearableDevices(careRecipientProfileId) }) { dtos -> 
            dtos.map { dto ->
                WearableDevice(
                    id = dto.id,
                    careRecipientId = dto.careRecipientProfileId,
                    serialNumber = dto.serialNumber,
                    deviceType = DeviceType.valueOf(dto.deviceType),
                    linkedAt = Instant.parse(dto.linkedAt)
                )

            }
        }
}
