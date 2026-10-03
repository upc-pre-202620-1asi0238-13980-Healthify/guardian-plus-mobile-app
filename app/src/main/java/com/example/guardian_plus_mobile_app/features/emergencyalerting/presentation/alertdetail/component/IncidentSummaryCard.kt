package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Incident
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatDuration
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.title
import java.time.Duration
import java.time.Instant

/** Facts of a stabilized or closed incident: what happened, when, how long and who answered. */
@Composable
fun IncidentSummaryCard(
    modifier: Modifier = Modifier,
    alert: Alert,
    incident: Incident,
    acknowledgedByName: String?,
    careRecipientFirstName: String
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.detail_summary_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            SummaryRow(
                label = stringResource(R.string.detail_summary_type),
                value = alert.sourceType.title(careRecipientFirstName)
            )
            SummaryRow(label = stringResource(R.string.detail_summary_start), value = formatClockTime(alert.triggeredAt))
            incident.stabilizedAt?.let {
                SummaryRow(label = stringResource(R.string.detail_summary_stabilized), value = formatClockTime(it))
            }
            incident.closedAt?.let {
                SummaryRow(label = stringResource(R.string.detail_summary_closed), value = formatClockTime(it))
            }
            val endedAt = incident.closedAt ?: incident.stabilizedAt
            endedAt?.let {
                SummaryRow(
                    label = stringResource(R.string.detail_summary_duration),
                    value = formatDuration(Duration.between(alert.triggeredAt, it).seconds)
                )
            }
            alert.acknowledgedAt?.let { acknowledgedAt ->
                val reaction = formatDuration(Duration.between(alert.lastDispatchedAt ?: alert.triggeredAt, acknowledgedAt).seconds.coerceAtLeast(0))
                SummaryRow(
                    label = stringResource(R.string.detail_summary_acknowledged_by),
                    value = stringResource(
                        R.string.detail_summary_acknowledged_value,
                        acknowledgedByName ?: stringResource(R.string.unknown_member),
                        reaction
                    )
                )
            }
            incident.notes?.takeIf { it.isNotBlank() }?.let {
                SummaryRow(label = stringResource(R.string.detail_summary_notes), value = it)
            }
        }
    }
}

@Composable
private fun SummaryRow(modifier: Modifier = Modifier, label: String, value: String) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IncidentSummaryCardPreview() {
    val start = Instant.parse("2026-10-03T19:05:00Z")
    GuardianTheme(dynamicColor = false) {
        IncidentSummaryCard(
            modifier = Modifier.padding(16.dp),
            alert = Alert(
                id = "1",
                careRecipientProfileId = "elena",
                sourceType = AlertSourceType.VITAL_SIGN_ANOMALY,
                severity = Severity.HIGH,
                status = AlertStatus.ACKNOWLEDGED,
                currentRecipientLevel = RecipientLevel.PRIMARY,
                triggeredAt = start,
                lastDispatchedAt = start,
                acknowledgedAt = start.plusSeconds(48)
            ),
            incident = Incident(
                id = "i1",
                alertId = "1",
                status = IncidentStatus.STABILIZED,
                markedInAttentionAt = start.plusSeconds(48),
                stabilizedAt = start.plusSeconds(1_080),
                closedAt = null,
                notes = null
            ),
            acknowledgedByName = "María Rojas",
            careRecipientFirstName = "Elena"
        )
    }
}
