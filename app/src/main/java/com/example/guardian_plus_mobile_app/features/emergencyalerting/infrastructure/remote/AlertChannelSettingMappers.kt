package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSetting
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.NotificationChannel

fun AlertChannelSettingDto.toDomain(): AlertChannelSetting = AlertChannelSetting(
    userId = userId,
    channel = NotificationChannel.valueOf(channel),
    enabled = enabled,
    deviceTokenRegistered = deviceTokenRegistered
)
