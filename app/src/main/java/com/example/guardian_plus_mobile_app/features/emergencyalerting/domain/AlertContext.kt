package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

/**
 * What other bounded contexts know about the moment of an alert (Health Monitoring readings,
 * Mobility location). Emergency & Alerting does not own this data; it only shows it.
 */
data class AlertContext(
    val readingSummary: String? = null,
    val locationName: String? = null,
    val locationUpdatedAgo: String? = null,
    val heartRateBpm: Int? = null,
    val oxygenSaturation: Int? = null,
    val deviceSummary: String? = null
)
