package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

data class UpdateAlertSettingsRequestDto(
    val primaryAckTimeoutSec: Int,
    val escalationEnabled: Boolean,
    val broadcastCriticalImmediately: Boolean
)
