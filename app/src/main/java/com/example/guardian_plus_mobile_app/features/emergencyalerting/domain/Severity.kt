package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

enum class Severity {
    CRITICAL,
    HIGH,
    MEDIUM;

    /** Only critical and high alerts escalate to the next contacts when nobody acknowledges them. */
    val allowsEscalation: Boolean get() = this == CRITICAL || this == HIGH
}
