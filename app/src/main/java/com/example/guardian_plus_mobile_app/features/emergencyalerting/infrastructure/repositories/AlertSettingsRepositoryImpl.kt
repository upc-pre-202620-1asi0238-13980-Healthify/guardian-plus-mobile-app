package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettings
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettingsRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.AlertSettingsService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.apiCall
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.toDomain
import javax.inject.Inject

class AlertSettingsRepositoryImpl @Inject constructor(
    private val service: AlertSettingsService
) : AlertSettingsRepository {

    override suspend fun getAlertSettings(careRecipientProfileId: String): Result<AlertSettings> =
        apiCall({ service.getAlertSettings(careRecipientProfileId) }) { dto -> dto.toDomain() }
}
