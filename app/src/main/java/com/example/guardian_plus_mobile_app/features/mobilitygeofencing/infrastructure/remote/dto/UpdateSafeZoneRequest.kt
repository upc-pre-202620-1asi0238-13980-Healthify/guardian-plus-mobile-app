package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateSafeZoneRequest(
    @SerializedName("name") val name: String,
    @SerializedName("centerLatitude") val centerLatitude: Double,
    @SerializedName("centerLongitude") val centerLongitude: Double,
    @SerializedName("radiusInMeters") val radiusInMeters: Double
)
