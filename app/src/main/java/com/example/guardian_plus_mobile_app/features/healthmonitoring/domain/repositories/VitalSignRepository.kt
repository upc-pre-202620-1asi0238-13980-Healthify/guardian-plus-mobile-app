package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice

interface VitalSignRepository {
    suspend fun getLiveVitalSigns(careRecipientProfileId: String): Result<LiveVitalSigns>
}
