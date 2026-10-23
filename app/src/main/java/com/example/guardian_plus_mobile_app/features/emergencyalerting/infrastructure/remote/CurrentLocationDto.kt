package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

data class CurrentLocationDto(
    val fragileCitizenId: String,
    val latitude: Double?,
    val longitude: Double?,
    val accuracyInMeters: Double?,
    val status: String?,
    val recordedAt: String?
)
