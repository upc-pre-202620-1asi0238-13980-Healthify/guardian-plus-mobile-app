package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

data class AlertDeliveryDto(
    val id: String,
    val recipientUserId: String,
    val recipientLevel: String,
    val channel: String,
    val deliveryStatus: String,
    val sentAt: String?,
    val deliveredAt: String?
)
