package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model

data class LocationHistoryItem(
    val latitude: Double,
    val longitude: Double,
    val accuracyInMeters: Double,
    val status: String,
    val recordedAt: String
)
