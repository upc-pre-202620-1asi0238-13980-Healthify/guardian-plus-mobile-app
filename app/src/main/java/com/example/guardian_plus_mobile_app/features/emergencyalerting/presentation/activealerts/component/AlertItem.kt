package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.ActiveAlertItem
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.SeverityBadge
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.colors
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.iconRes
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.relativeTime
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.title
import java.time.Instant

/** Row of the "Otras alertas activas" list (component AlertaItem of the prototype). */
@Composable
fun AlertItem(
    modifier: Modifier = Modifier,
    item: ActiveAlertItem,
    careRecipientFirstName: String,
    now: Instant,
    onClick: () -> Unit
) {
    val alert = item.alert
    val severityColors = alert.severity.colors()

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(severityColors.container, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(alert.sourceType.iconRes()),
                    contentDescription = null,
                    tint = severityColors.content,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = alert.sourceType.title(careRecipientFirstName),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    SeverityBadge(severity = alert.severity)
                }
                val detail = listOfNotNull(item.context.readingSummary, item.context.locationName)
                if (detail.isNotEmpty()) {
                    Text(
                        text = detail.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AlertMeta(alert = alert, now = now)
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.active_alerts_open_detail),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AlertMeta(modifier: Modifier = Modifier, alert: Alert, now: Instant) {
    val elapsed = relativeTime(alert.triggeredAt, now)
    val (text, color) = when {
        alert.status.isAwaitingAcknowledgement ->
            stringResource(R.string.active_alerts_meta_unacknowledged, elapsed) to MaterialTheme.colorScheme.tertiary
        alert.status == AlertStatus.PENDING_CONFIRMATION ->
            stringResource(R.string.active_alerts_meta_confirming, elapsed) to MaterialTheme.colorScheme.onSurfaceVariant
        else ->
            stringResource(R.string.active_alerts_meta_acknowledged, elapsed) to MaterialTheme.colorScheme.secondary
    }
    Row(
        modifier = modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertItemPreview() {
    val now = Instant.parse("2026-10-03T14:33:05Z")
    GuardianTheme(dynamicColor = false) {
        AlertItem(
            modifier = Modifier.padding(16.dp),
            item = ActiveAlertItem(
                alert = Alert(
                    id = "2",
                    careRecipientProfileId = "elena",
                    sourceType = AlertSourceType.VITAL_SIGN_ANOMALY,
                    severity = Severity.HIGH,
                    status = AlertStatus.TRIGGERED,
                    currentRecipientLevel = RecipientLevel.PRIMARY,
                    triggeredAt = now.minusSeconds(360)
                ),
                context = AlertContext(readingSummary = "124 lpm · 3 lecturas fuera de rango")
            ),
            careRecipientFirstName = "Elena",
            now = now,
            onClick = {}
        )
    }
}
