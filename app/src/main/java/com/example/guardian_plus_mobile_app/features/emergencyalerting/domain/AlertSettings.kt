package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

data class AlertSettings(
    val careRecipientProfileId: String,
    val primaryAckTimeoutSec: Int,
    val escalationEnabled: Boolean,
    val silentModeEnabled: Boolean,
    val broadcastCriticalImmediately: Boolean
)
