package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

/** Item of GET /alerts/active/... and GET /alerts/pending/... (AlertSummaryResource). */
data class AlertSummaryDto(
    val id: String,
    val careRecipientProfileId: String,
    val sourceType: String,
    val severity: String,
    val status: String,
    // null while a fall waits for its 20 s confirmation window: it was not sent to anyone yet
    val currentRecipientLevel: String?,
    val triggeredAt: String,
    val acknowledgedAt: String?
)
