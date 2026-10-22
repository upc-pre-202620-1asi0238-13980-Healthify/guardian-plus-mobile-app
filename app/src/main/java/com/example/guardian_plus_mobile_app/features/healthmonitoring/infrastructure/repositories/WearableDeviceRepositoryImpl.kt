package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.DeviceType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.WearableDeviceRepository
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto.LinkWearableDeviceRequestDto
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto.WearableDeviceDto
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.services.WearableDeviceService
import java.time.Instant
import javax.inject.Inject

class WearableDeviceRepositoryImpl @Inject constructor(
    private val service: WearableDeviceService
) : WearableDeviceRepository {

    override suspend fun getWearableDevices(careRecipientProfileId: String): Result<List<WearableDevice>> =
        apiCall({ service.getWearableDevices(careRecipientProfileId) }) { dtos -> dtos.map { it.toDomain() } }

    override suspend fun linkWearableDevice(
        careRecipientProfileId: String,
        serialNumber: String,
        deviceType: DeviceType
    ): Result<WearableDevice> =
        apiCall({
            service.linkWearableDevice(LinkWearableDeviceRequestDto(careRecipientProfileId, serialNumber, deviceType.name))
        }) { it.toDomain() }

    private fun WearableDeviceDto.toDomain() = WearableDevice(
        id = id,
        careRecipientId = careRecipientProfileId,
        serialNumber = serialNumber,
        deviceType = DeviceType.valueOf(deviceType),
        linkedAt = Instant.parse(linkedAt)
    )
}
