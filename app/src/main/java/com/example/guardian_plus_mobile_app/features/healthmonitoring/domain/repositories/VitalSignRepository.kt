package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice
import java.time.LocalDate

interface VitalSignRepository {
    suspend fun getLiveVitalSigns(careRecipientProfileId: String): Result<LiveVitalSigns>
    suspend fun getVitalSignHistory(careRecipientProfileId: String, from: LocalDate, to: LocalDate): Result<List<VitalSignReading>>

}
