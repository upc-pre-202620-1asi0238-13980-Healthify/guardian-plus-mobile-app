package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

/** deviceToken is left out: push delivery is still simulated by the platform. */
data class ConfigureAlertChannelRequestDto(
    val enabled: Boolean
)
