package com.example.guardian_plus_mobile_app.features.healthmonitoring.application

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.WearableDeviceRepository
import javax.inject.Inject

class GetWearableDevicesUseCase @Inject constructor( 
    private val repository: WearableDeviceRepository
) {
    suspend operator fun invoke(careRecipientProfileId: String): Result<List<WearableDevice>> =
        repository.getWearableDevices(careRecipientProfileId)
}
