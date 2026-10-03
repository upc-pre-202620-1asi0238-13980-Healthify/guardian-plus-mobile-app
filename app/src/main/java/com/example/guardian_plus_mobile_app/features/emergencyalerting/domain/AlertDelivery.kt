package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

import java.time.Instant

data class AlertDelivery(
    val id: String,
    val recipientUserId: String,
    val recipientLevel: RecipientLevel,
    val channel: NotificationChannel,
    val status: DeliveryStatus,
    val sentAt: Instant?,
    val deliveredAt: Instant?
)
