package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName

data class SafeZoneDto(
    @SerializedName("id") val id: String,
    @SerializedName("fragileCitizenId") val fragileCitizenId: String,
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("radiusInMeters") val radiusInMeters: Double,
    @SerializedName("status") val status: String
)
