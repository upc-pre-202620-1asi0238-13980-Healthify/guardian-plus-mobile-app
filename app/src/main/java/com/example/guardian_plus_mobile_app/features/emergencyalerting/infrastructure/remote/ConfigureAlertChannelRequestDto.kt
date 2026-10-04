package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

/** Turns a channel on or off; the app does not register push device tokens. */
data class ConfigureAlertChannelRequestDto(
    val enabled: Boolean
)
