package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

data class IncidentDto(
    val id: String,
    val alertId: String,
    val status: String,
    val markedInAttentionAt: String,
    val stabilizedAt: String?,
    val closedAt: String?,
    val notes: String?
)
