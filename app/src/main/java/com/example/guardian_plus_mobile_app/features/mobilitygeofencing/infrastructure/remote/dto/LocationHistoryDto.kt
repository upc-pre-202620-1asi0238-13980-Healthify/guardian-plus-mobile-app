package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName

data class LocationHistoryDto(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("accuracyInMeters") val accuracyInMeters: Double,
    @SerializedName("status") val status: String,
    @SerializedName("recordedAt") val recordedAt: String
)
