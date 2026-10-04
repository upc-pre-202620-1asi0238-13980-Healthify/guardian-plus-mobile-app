package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

/** A member who took charge of going to help: on the way, arrived, or cancelled when the alert was resolved. */
enum class ResponseStatus {
    CLAIMED,
    COMPLETED,
    CANCELLED
}
