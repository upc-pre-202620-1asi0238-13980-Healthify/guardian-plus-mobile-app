package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

/** What raised the alert: the wristband (fall, SOS) or another bounded context. */
enum class AlertSourceType {
    FALL_DETECTED,
    SOS_TRIGGERED,
    VITAL_SIGN_ANOMALY,
    SAFE_ZONE_VIOLATION,
    PROLONGED_INACTIVITY,
    REMINDER_REISSUED,
    MEDICATION_RESTOCK_SUGGESTED
}
