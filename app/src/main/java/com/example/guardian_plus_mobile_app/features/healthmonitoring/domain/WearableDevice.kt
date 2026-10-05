package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

import java.time.Instant

data class WearableDevice( 
    val id: String,
    val careRecipientId: String,
    val serialNumber: String,
    val deviceType: DeviceType,
    val linkedAt: Instant
)
