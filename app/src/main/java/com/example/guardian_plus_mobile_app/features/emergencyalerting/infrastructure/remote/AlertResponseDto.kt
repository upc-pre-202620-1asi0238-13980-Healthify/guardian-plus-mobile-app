package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

data class AlertResponseDto(
    val id: String,
    val responderUserId: String,
    val responseStatus: String,
    val claimedAt: String,
    val completedAt: String?,
    val notes: String?
)
