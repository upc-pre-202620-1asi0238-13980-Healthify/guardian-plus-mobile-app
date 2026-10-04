package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

data class AlertSettingsDto(
    val careRecipientProfileId: String,
    val primaryAckTimeoutSec: Int,
    val escalationEnabled: Boolean,
    val silentModeEnabled: Boolean,
    val broadcastCriticalImmediately: Boolean
)
