package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

/** One page of the alert history, most recent first. */
data class AlertPage(
    val alerts: List<Alert>,
    val page: Int,
    val totalPages: Int,
    val totalElements: Long
) {
    val isLast: Boolean get() = page >= totalPages - 1
}
