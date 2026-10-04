package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.escalationSecondsLeft
import java.time.Instant

/** An active alert plus what the screen needs to draw it. */
data class ActiveAlertItem(
    val alert: Alert,
    val context: AlertContext = AlertContext(),
    // The backend only accepts the acknowledgement of members the alert was sent to
    val awaitsMyAcknowledgement: Boolean = false
)

data class ActiveAlertsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val careRecipientFirstName: String = "",
    // The most urgent critical alert, drawn as the red card at the top
    val featuredAlert: ActiveAlertItem? = null,
    val otherAlerts: List<ActiveAlertItem> = emptyList(),
    val ackTimeoutSec: Int = 60,
    val escalationEnabled: Boolean = true,
    val acknowledgingAlertId: String? = null,
    val actionErrorMessage: String? = null,
    // Set after a successful acknowledgement so the screen opens that alert (emergency route, step 3)
    val acknowledgedAlertId: String? = null,
    // Ticks every second so relative times and the countdown keep moving
    val now: Instant = Instant.now()
) {
    val activeCount: Int get() = otherAlerts.size + if (featuredAlert != null) 1 else 0

    /** Seconds left before the featured alert escalates to the next contacts, or null when it will not escalate. */
    val escalationSecondsLeft: Long?
        get() = featuredAlert?.alert?.escalationSecondsLeft(ackTimeoutSec, escalationEnabled, now)
}
