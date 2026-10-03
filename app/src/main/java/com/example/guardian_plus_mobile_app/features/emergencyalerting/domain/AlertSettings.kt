package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

data class AlertSettings(
    val careRecipientProfileId: String,
    val primaryAckTimeoutSec: Int,
    val escalationEnabled: Boolean,
    // Non-critical alerts reach the Care Circle phones without sound (US22)
    val silentModeEnabled: Boolean,
    val broadcastCriticalImmediately: Boolean
)

/** Limits the platform accepts for the acknowledgement timeout (AckTimeout value object). */
object AckTimeoutLimits {
    const val MIN_SECONDS = 15
    const val MAX_SECONDS = 300
}
