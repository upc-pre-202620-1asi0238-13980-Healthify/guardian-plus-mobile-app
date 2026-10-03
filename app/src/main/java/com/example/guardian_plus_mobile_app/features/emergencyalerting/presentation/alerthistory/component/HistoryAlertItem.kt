package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.StatusChip
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.colors
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatDuration
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.iconRes
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.labelRes
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.title
import java.time.Duration
import java.time.Instant

/** Row of the history (component AlertaHistorialItem of the prototype): what happened, when and how it ended. */
@Composable
fun HistoryAlertItem(
    modifier: Modifier = Modifier,
    alert: Alert,
    careRecipientFirstName: String,
    onClick: () -> Unit
) {
    // Finished alerts drop the severity color: green when someone answered, grey when it was dismissed
    val (iconContainer, iconContent) = when (alert.status) {
        AlertStatus.RESOLVED -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        AlertStatus.DISMISSED -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        else -> alert.severity.colors().let { it.container to it.content }
    }
    val (chipContainer, chipContent) = alert.status.colors()

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(alert.sourceType.iconRes()),
                    contentDescription = null,
                    tint = iconContent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alert.sourceType.title(careRecipientFirstName),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatClockTime(alert.triggeredAt),
                        style = MaterialTheme.typography.dataLabel,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = outcome(alert, careRecipientFirstName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                StatusChip(
                    modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(alert.status.labelRes()),
                    container = chipContainer,
                    content = chipContent
                )
            }
        }
    }
}

/** One sentence on how the alert ended, from what the list endpoint returns. */
@Composable
private fun outcome(alert: Alert, careRecipientFirstName: String): String = when {
    alert.status == AlertStatus.DISMISSED ->
        stringResource(R.string.history_dismissed_detail, careRecipientFirstName)
    alert.acknowledgedAt != null ->
        stringResource(
            R.string.history_acknowledged_in,
            formatDuration(Duration.between(alert.triggeredAt, alert.acknowledgedAt).seconds.coerceAtLeast(0))
        )
    alert.status == AlertStatus.PENDING_CONFIRMATION ->
        stringResource(R.string.history_waiting_confirmation, careRecipientFirstName)
    else -> stringResource(R.string.history_waiting_acknowledgement)
}

@Preview(showBackground = true)
@Composable
private fun HistoryAlertItemPreview() {
    val start = Instant.parse("2026-10-03T22:58:00Z")
    GuardianTheme(dynamicColor = false) {
        HistoryAlertItem(
            modifier = Modifier.padding(16.dp),
            alert = Alert(
                id = "1",
                careRecipientProfileId = "elena",
                sourceType = AlertSourceType.SOS_TRIGGERED,
                severity = Severity.CRITICAL,
                status = AlertStatus.RESOLVED,
                currentRecipientLevel = RecipientLevel.PRIMARY,
                triggeredAt = start,
                acknowledgedAt = start.plusSeconds(32)
            ),
            careRecipientFirstName = "Elena",
            onClick = {}
        )
    }
}
