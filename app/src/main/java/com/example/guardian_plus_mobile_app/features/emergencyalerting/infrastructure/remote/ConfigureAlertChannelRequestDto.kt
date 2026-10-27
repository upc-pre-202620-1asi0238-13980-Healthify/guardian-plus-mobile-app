package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

/** Turns a channel on or off; [deviceToken] is only sent for PUSH, to register this phone's FCM token. */
data class ConfigureAlertChannelRequestDto(
    val enabled: Boolean,
    val deviceToken: String? = null
)
