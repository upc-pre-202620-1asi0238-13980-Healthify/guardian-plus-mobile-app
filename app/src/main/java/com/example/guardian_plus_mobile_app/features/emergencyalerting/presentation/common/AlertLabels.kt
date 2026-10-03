package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Domain values → interface labels, following the label tables of the report (section 3.1.2.2)

@StringRes
fun Severity.labelRes(): Int = when (this) {
    Severity.CRITICAL -> R.string.severity_critical
    Severity.HIGH -> R.string.severity_high
    Severity.MEDIUM -> R.string.severity_medium
}

@StringRes
fun AlertStatus.labelRes(): Int = when (this) {
    AlertStatus.PENDING_CONFIRMATION -> R.string.status_pending_confirmation
    AlertStatus.TRIGGERED -> R.string.status_triggered
    AlertStatus.ESCALATED -> R.string.status_escalated
    AlertStatus.ACKNOWLEDGED -> R.string.status_acknowledged
    AlertStatus.DISMISSED -> R.string.status_dismissed
    AlertStatus.RESOLVED -> R.string.status_resolved
}

@StringRes
fun IncidentStatus.labelRes(): Int = when (this) {
    IncidentStatus.IN_ATTENTION -> R.string.incident_in_attention
    IncidentStatus.STABILIZED -> R.string.incident_stabilized
    IncidentStatus.CLOSED -> R.string.incident_closed
}

@StringRes
fun AlertSourceType.tagRes(): Int = when (this) {
    AlertSourceType.FALL_DETECTED -> R.string.source_fall_tag
    AlertSourceType.SOS_TRIGGERED -> R.string.source_sos_tag
    AlertSourceType.VITAL_SIGN_ANOMALY -> R.string.source_vital_sign_tag
    AlertSourceType.SAFE_ZONE_VIOLATION -> R.string.source_safe_zone_tag
    AlertSourceType.PROLONGED_INACTIVITY -> R.string.source_inactivity_tag
    AlertSourceType.REMINDER_REISSUED -> R.string.source_reminder_tag
    AlertSourceType.MEDICATION_RESTOCK_SUGGESTED -> R.string.source_medication_tag
}

@DrawableRes
fun AlertSourceType.iconRes(): Int = when (this) {
    AlertSourceType.FALL_DETECTED -> R.drawable.ic_triangle_alert
    AlertSourceType.SOS_TRIGGERED -> R.drawable.ic_siren
    AlertSourceType.VITAL_SIGN_ANOMALY -> R.drawable.ic_activity
    AlertSourceType.SAFE_ZONE_VIOLATION -> R.drawable.ic_map_pin
    AlertSourceType.PROLONGED_INACTIVITY -> R.drawable.ic_hourglass
    AlertSourceType.REMINDER_REISSUED -> R.drawable.ic_bell
    AlertSourceType.MEDICATION_RESTOCK_SUGGESTED -> R.drawable.ic_pill
}

/** Title of an alert; the SOS one names the person under care ("Elena presionó el botón SOS"). */
@Composable
fun AlertSourceType.title(careRecipientFirstName: String): String = when (this) {
    AlertSourceType.FALL_DETECTED -> stringResource(R.string.source_fall_title)
    AlertSourceType.SOS_TRIGGERED -> stringResource(R.string.source_sos_title, careRecipientFirstName)
    AlertSourceType.VITAL_SIGN_ANOMALY -> stringResource(R.string.source_vital_sign_title)
    AlertSourceType.SAFE_ZONE_VIOLATION -> stringResource(R.string.source_safe_zone_title)
    AlertSourceType.PROLONGED_INACTIVITY -> stringResource(R.string.source_inactivity_title)
    AlertSourceType.REMINDER_REISSUED -> stringResource(R.string.source_reminder_title)
    AlertSourceType.MEDICATION_RESTOCK_SUGGESTED -> stringResource(R.string.source_medication_title)
}

/** "Hace 42 s", "Hace 6 min"… measured against the screen clock so it keeps moving. */
@Composable
fun relativeTime(from: Instant, now: Instant): String {
    val seconds = Duration.between(from, now).seconds.coerceAtLeast(0)
    return when {
        seconds < 5 -> stringResource(R.string.time_just_now)
        seconds < 60 -> stringResource(R.string.time_seconds_ago, seconds.toInt())
        seconds < 3_600 -> stringResource(R.string.time_minutes_ago, (seconds / 60).toInt())
        seconds < 86_400 -> stringResource(R.string.time_hours_ago, (seconds / 3_600).toInt())
        else -> stringResource(R.string.time_days_ago, (seconds / 86_400).toInt())
    }
}

/** 42 → "0:42", the escalation countdown format of the prototype. */
fun formatCountdown(seconds: Long): String = "%d:%02d".format(seconds / 60, seconds % 60)

private val clockWithSeconds: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
private val clock: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/** Wall-clock time on the phone's time zone: "14:32:05", or "14:32" without seconds. */
fun formatClockTime(instant: Instant, withSeconds: Boolean = false): String =
    (if (withSeconds) clockWithSeconds else clock).format(instant.atZone(ZoneId.systemDefault()))

/** "48 s", "18 min" or "1 h 05 min". */
@Composable
fun formatDuration(seconds: Long): String = when {
    seconds < 60 -> stringResource(R.string.duration_seconds, seconds.toInt())
    seconds < 3_600 -> stringResource(R.string.duration_minutes, (seconds / 60).toInt())
    else -> stringResource(R.string.duration_hours, (seconds / 3_600).toInt(), ((seconds % 3_600) / 60).toInt())
}
