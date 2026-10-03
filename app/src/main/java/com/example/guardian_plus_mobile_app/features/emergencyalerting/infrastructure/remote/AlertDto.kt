package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

/** Response of GET /alerts/{id} and of the alert commands (AlertResource). */
data class AlertDto(
    val id: String,
    val careRecipientProfileId: String,
    val sourceType: String,
    val sourceReferenceId: String,
    val severity: String,
    val status: String,
    val currentRecipientLevel: String?,
    val triggeredAt: String,
    val confirmedAt: String?,
    val lastDispatchedAt: String?,
    val acknowledgedAt: String?,
    val acknowledgedByUserId: String?,
    val resolvedAt: String?,
    val deliveries: List<AlertDeliveryDto>?,
    val responses: List<AlertResponseDto>?
)
