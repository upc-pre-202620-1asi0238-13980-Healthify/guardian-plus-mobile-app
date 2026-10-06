package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto


data class VitalSignDto(
    val id: String,
    val vitalSignType: String,
    val value: Double,
    val measuredAt: String
)
