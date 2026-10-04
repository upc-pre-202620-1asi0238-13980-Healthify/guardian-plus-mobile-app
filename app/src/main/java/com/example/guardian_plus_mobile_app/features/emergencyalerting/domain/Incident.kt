package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

import java.time.Instant

/** Opened when someone acknowledges an alert; closing it resolves the alert. */
data class Incident(
    val id: String,
    val alertId: String,
    val status: IncidentStatus,
    val markedInAttentionAt: Instant,
    val stabilizedAt: Instant?,
    val closedAt: Instant?,
    val notes: String?
)
