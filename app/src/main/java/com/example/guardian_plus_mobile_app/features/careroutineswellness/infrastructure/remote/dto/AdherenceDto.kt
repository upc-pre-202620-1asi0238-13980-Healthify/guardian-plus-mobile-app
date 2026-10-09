package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto

// The platform also sends the day-by-day breakdown, which the app does not use yet
data class AdherenceDto(
    val type: String,
    val from: String,
    val to: String,
    val due: Int,
    val confirmed: Int,
    val percentage: Int
)
