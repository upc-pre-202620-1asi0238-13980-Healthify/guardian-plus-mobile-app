package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto

data class WearableDeviceDto(
    val id: String,
    val careRecipientProfileId: String,
    val serialNumber: String,
    val deviceType: String,
    val linkedAt: String
)
