package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

import java.time.Instant

data class Alert(
    val id: String,
    val careRecipientProfileId: String,
    val sourceType: AlertSourceType,
    val severity: Severity,
    val status: AlertStatus,
    // null until the alert is dispatched for the first time
    val currentRecipientLevel: RecipientLevel?,
    val triggeredAt: Instant,
    val acknowledgedAt: Instant? = null,
    // The fields below are only present when the alert is read in full (GET /alerts/{id}), not in list summaries
    val confirmedAt: Instant? = null,
    val lastDispatchedAt: Instant? = null,
    val acknowledgedByUserId: String? = null,
    val resolvedAt: Instant? = null,
    val deliveries: List<AlertDelivery> = emptyList(),
    val responses: List<AlertResponse> = emptyList()
) {
    /** The member currently on the way, if any; the backend allows only one at a time. */
    val activeResponse: AlertResponse? get() = responses.firstOrNull { it.status == ResponseStatus.CLAIMED }
}
