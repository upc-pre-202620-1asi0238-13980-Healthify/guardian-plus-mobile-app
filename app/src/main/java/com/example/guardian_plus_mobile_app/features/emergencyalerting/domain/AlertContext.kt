package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

import java.time.Instant

/**
 * What other bounded contexts know about the moment of an alert (Health Monitoring readings,
 * Mobility location). Emergency & Alerting does not own this data; it only shows it.
 */
data class AlertContext(
    val readingSummary: String? = null,
    val locationName: String? = null,
    // When the wristband sent the location, so the screen can say how long ago with its own clock
    val locationUpdatedAt: Instant? = null,
    val heartRateBpm: Int? = null,
    val oxygenSaturation: Int? = null,
    val deviceSummary: String? = null
)
