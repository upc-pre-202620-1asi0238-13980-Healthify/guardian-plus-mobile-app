package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice


interface WearableDeviceRepository {
    suspend fun getWearableDevices(careRecipientProfileId: String): Result<List<WearableDevice>>
}
