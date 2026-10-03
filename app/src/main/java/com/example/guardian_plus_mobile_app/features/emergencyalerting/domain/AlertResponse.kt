package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

import java.time.Instant

data class AlertResponse(
    val id: String,
    val responderUserId: String,
    val status: ResponseStatus,
    val claimedAt: Instant,
    val completedAt: Instant?,
    val notes: String?
)
