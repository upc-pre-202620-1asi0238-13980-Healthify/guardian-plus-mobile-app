package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.FallConfirmationWindow
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Incident
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatCountdown
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatDuration
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.iconRes
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.labelRes
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.relativeTime
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.tagRes
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.title
import java.time.Duration
import java.time.Instant

/**
 * Top card of the detail. Red for critical alerts still in play (with the SOS variant of US15),
 * orange or yellow for high and medium ones, and green once the incident is stabilized or over.
 */
@Composable
fun AlertHeroCard(
    modifier: Modifier = Modifier,
    alert: Alert,
    incident: Incident?,
    context: AlertContext,
    careRecipientFirstName: String,
    confirmationSecondsLeft: Long?,
    now: Instant
) {
    val calm = incident?.status == IncidentStatus.STABILIZED ||
        incident?.status == IncidentStatus.CLOSED ||
        alert.status == AlertStatus.RESOLVED ||
        alert.status == AlertStatus.DISMISSED
    val (container, content) = heroColors(alert.severity, calm)
    val isActiveSos = alert.sourceType == AlertSourceType.SOS_TRIGGERED && !calm

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = container,
        contentColor = content,
        shadowElevation = if (calm) 2.dp else 6.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tag = if (isActiveSos) {
                    stringResource(R.string.detail_max_priority)
                } else {
                    stringResource(
                        R.string.tag_separator,
                        stringResource(alert.sourceType.tagRes()),
                        stringResource(alert.severity.labelRes())
                    )
                }
                Text(
                    text = tag.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .background(content.copy(alpha = 0.18f), MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
                Text(
                    text = if (calm) formatClockTime(alert.triggeredAt) else relativeTime(alert.triggeredAt, now),
                    style = MaterialTheme.typography.dataLabel
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isActiveSos) {
                SosHeadline(careRecipientFirstName = careRecipientFirstName, content = content, container = container)
            } else {
                IconHeadline(
                    alert = alert,
                    incident = incident,
                    context = context,
                    careRecipientFirstName = careRecipientFirstName,
                    content = content
                )
            }

            if (!isActiveSos) {
                Spacer(modifier = Modifier.height(16.dp))
                HeroTiles(
                    alert = alert,
                    incident = incident,
                    calm = calm,
                    confirmationSecondsLeft = confirmationSecondsLeft,
                    now = now,
                    content = content
                )
            }
        }
    }
}

@Composable
private fun heroColors(severity: Severity, calm: Boolean): Pair<Color, Color> = when {
    calm -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    severity == Severity.CRITICAL -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    severity == Severity.HIGH -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
    else -> MaterialTheme.colorScheme.noticeContainer to MaterialTheme.colorScheme.onNoticeContainer
}

@Composable
private fun SosHeadline(
    modifier: Modifier = Modifier,
    careRecipientFirstName: String,
    content: Color,
    container: Color
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Round pulse: the shape the style guide reserves for SOS
        Box(
            modifier = Modifier
                .size(112.dp)
                .background(content.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(content, CircleShape)
                    .border(2.dp, content, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.source_sos_tag),
                    color = container,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
        Text(
            text = stringResource(R.string.source_sos_title, careRecipientFirstName),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.detail_sos_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = content.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun IconHeadline(
    modifier: Modifier = Modifier,
    alert: Alert,
    incident: Incident?,
    context: AlertContext,
    careRecipientFirstName: String,
    content: Color
) {
    val title = when {
        alert.status == AlertStatus.DISMISSED -> stringResource(R.string.detail_dismissed_title, careRecipientFirstName)
        incident?.status == IncidentStatus.CLOSED || alert.status == AlertStatus.RESOLVED ->
            stringResource(R.string.detail_closed_title)
        incident?.status == IncidentStatus.STABILIZED -> stringResource(R.string.detail_stabilized_title)
        alert.sourceType == AlertSourceType.FALL_DETECTED && alert.status != AlertStatus.PENDING_CONFIRMATION ->
            stringResource(R.string.detail_fall_unanswered_title, careRecipientFirstName)
        else -> alert.sourceType.title(careRecipientFirstName)
    }
    val detail = if (alert.sourceType == AlertSourceType.FALL_DETECTED) {
        stringResource(R.string.detail_fall_subtitle)
    } else {
        context.readingSummary
    }
    val subtitle = listOfNotNull(
        alert.sourceType.title(careRecipientFirstName).takeIf { title != it && incident?.status != null },
        detail,
        context.locationName?.substringBefore(" · ")
    ).joinToString(" · ")

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(content.copy(alpha = 0.18f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(alert.sourceType.iconRes()),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = content.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun HeroTiles(
    modifier: Modifier = Modifier,
    alert: Alert,
    incident: Incident?,
    calm: Boolean,
    confirmationSecondsLeft: Long?,
    now: Instant,
    content: Color
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        if (calm) {
            val endedAt = incident?.closedAt ?: incident?.stabilizedAt ?: alert.resolvedAt ?: now
            HeroTile(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.detail_tile_detected),
                value = formatClockTime(alert.triggeredAt),
                content = content
            )
            HeroTile(
                modifier = Modifier.weight(1f),
                label = stringResource(
                    if (incident?.status == IncidentStatus.STABILIZED) R.string.detail_tile_stabilized else R.string.detail_tile_closed
                ),
                value = formatClockTime(endedAt),
                content = content
            )
            HeroTile(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.detail_tile_duration),
                value = formatDuration(Duration.between(alert.triggeredAt, endedAt).seconds),
                content = content
            )
        } else {
            if (alert.sourceType == AlertSourceType.FALL_DETECTED) {
                HeroTile(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.detail_tile_confirmation_window),
                    value = if (confirmationSecondsLeft != null) {
                        stringResource(R.string.detail_tile_confirmation_left, confirmationSecondsLeft.toInt())
                    } else {
                        stringResource(R.string.detail_tile_confirmation_expired, FallConfirmationWindow.SECONDS.toInt())
                    },
                    content = content
                )
            } else {
                HeroTile(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.detail_tile_detected),
                    value = formatClockTime(alert.triggeredAt),
                    content = content
                )
            }
            HeroTile(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.detail_tile_since_detection),
                value = formatCountdown(Duration.between(alert.triggeredAt, now).seconds.coerceAtLeast(0)),
                content = content
            )
        }
    }
}

@Composable
private fun HeroTile(modifier: Modifier = Modifier, label: String, value: String, content: Color) {
    Column(
        modifier = modifier
            .background(content.copy(alpha = 0.14f), MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = content.copy(alpha = 0.85f))
        Text(text = value, style = MaterialTheme.typography.dataLabel, modifier = Modifier.padding(top = 2.dp))
    }
}

private val previewNow: Instant = Instant.parse("2026-10-03T14:33:17Z")

@Preview(showBackground = true)
@Composable
private fun AlertHeroCardFallPreview() {
    GuardianTheme(dynamicColor = false) {
        AlertHeroCard(
            modifier = Modifier.padding(16.dp),
            alert = Alert(
                id = "1",
                careRecipientProfileId = "elena",
                sourceType = AlertSourceType.FALL_DETECTED,
                severity = Severity.CRITICAL,
                status = AlertStatus.TRIGGERED,
                currentRecipientLevel = RecipientLevel.PRIMARY,
                triggeredAt = previewNow.minusSeconds(72)
            ),
            incident = null,
            context = AlertContext(locationName = "Dormitorio · Casa de Elena"),
            careRecipientFirstName = "Elena",
            confirmationSecondsLeft = null,
            now = previewNow
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertHeroCardSosPreview() {
    GuardianTheme(dynamicColor = false) {
        AlertHeroCard(
            modifier = Modifier.padding(16.dp),
            alert = Alert(
                id = "2",
                careRecipientProfileId = "elena",
                sourceType = AlertSourceType.SOS_TRIGGERED,
                severity = Severity.CRITICAL,
                status = AlertStatus.TRIGGERED,
                currentRecipientLevel = RecipientLevel.BROADCAST,
                triggeredAt = previewNow.minusSeconds(20)
            ),
            incident = null,
            context = AlertContext(),
            careRecipientFirstName = "Elena",
            confirmationSecondsLeft = null,
            now = previewNow
        )
    }
}
