package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

enum class AlertStatus {
    PENDING_CONFIRMATION,
    TRIGGERED,
    ESCALATED,
    ACKNOWLEDGED,
    DISMISSED,
    RESOLVED;

    /** A Care Circle member can acknowledge the alert only in these states. */
    val isAwaitingAcknowledgement: Boolean get() = this == TRIGGERED || this == ESCALATED
}
