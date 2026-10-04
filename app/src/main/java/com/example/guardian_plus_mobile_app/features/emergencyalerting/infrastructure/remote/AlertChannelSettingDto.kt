package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

data class AlertChannelSettingDto(
    val userId: String,
    val channel: String,
    val enabled: Boolean,
    val deviceTokenRegistered: Boolean
)
