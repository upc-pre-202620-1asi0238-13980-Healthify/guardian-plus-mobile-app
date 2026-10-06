package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSetting
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSettingRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.NotificationChannel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.AlertChannelSettingService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.ConfigureAlertChannelRequestDto
import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.toDomain
import javax.inject.Inject

class AlertChannelSettingRepositoryImpl @Inject constructor(
    private val service: AlertChannelSettingService
) : AlertChannelSettingRepository {

    override suspend fun getChannelSettings(userId: String): Result<List<AlertChannelSetting>> =
        apiCall({ service.getChannelSettings(userId) }) { dtos -> dtos.map { it.toDomain() } }

    override suspend fun configureChannel(
        userId: String,
        channel: NotificationChannel,
        enabled: Boolean
    ): Result<AlertChannelSetting> =
        apiCall({ service.configureChannel(userId, channel.name, ConfigureAlertChannelRequestDto(enabled)) }) { dto ->
            dto.toDomain()
        }
}
