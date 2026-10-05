package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

import java.time.Instant

data class LiveVitalSigns(
    val careRecipientProfileId: String,
    val retrievedAt: Instant,
    val vitalSigns: List<LiveVitalSign> // List of the vital sign data class defined
) {
    // Latest Reading of any type, null if the wearable never sent one
    operator fun get(type: VitalSignType): LiveVitalSign? = vitalSigns.firstOrNull {it.type == type}

    //All signals within normal range
    val allWithinRange: Boolean get() = vitalSigns.none { it.classification.isOutRange }

    val hasLiveSignal: Boolean get() = vitalSigns.any {it.liveSignal}
}
