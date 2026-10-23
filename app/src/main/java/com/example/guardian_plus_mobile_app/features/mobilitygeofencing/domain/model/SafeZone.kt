package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model

data class SafeZone(
    val id: String,
    val fragileCitizenId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusInMeters: Double,
    val status: String
)
