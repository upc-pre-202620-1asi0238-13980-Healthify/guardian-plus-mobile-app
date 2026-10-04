package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

/** Outcome of sending an alert to one Care Circle member through one channel. */
enum class DeliveryStatus {
    PENDING,
    SENT,
    DELIVERED,
    FAILED
}
