package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.ActiveAlertItem
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatCountdown
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.iconRes
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.labelRes
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.relativeTime
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.tagRes
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.title
import java.time.Instant

// Two buttons share the card width, so they use less side padding than Material's default 24 dp
private val CompactButtonPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

/** Red card of the most urgent critical alert, with the escalation countdown and the two quick actions. */
@Composable
fun CriticalAlertCard(
    modifier: Modifier = Modifier,
    item: ActiveAlertItem,
    careRecipientFirstName: String,
    now: Instant,
    escalationSecondsLeft: Long?,
    isAcknowledging: Boolean,
    onClick: () -> Unit,
    onAcknowledgeClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val alert = item.alert
    val onCard = MaterialTheme.colorScheme.onError

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.error,
        contentColor = onCard,
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.tag_separator,
                        stringResource(alert.severity.labelRes()),
                        stringResource(alert.sourceType.tagRes())
                    ).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .background(onCard.copy(alpha = 0.18f), MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
                Text(
                    text = relativeTime(alert.triggeredAt, now),
                    style = MaterialTheme.typography.dataLabel
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(onCard.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(alert.sourceType.iconRes()),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = alert.sourceType.title(careRecipientFirstName),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = subtitle(item, careRecipientFirstName),
                        style = MaterialTheme.typography.bodyMedium,
                        color = onCard.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            EscalationStatus(
                modifier = Modifier.padding(top = 14.dp),
                alert = alert,
                escalationSecondsLeft = escalationSecondsLeft
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (item.awaitsMyAcknowledgement) {
                    Button(
                        onClick = onAcknowledgeClick,
                        enabled = !isAcknowledging,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = MaterialTheme.shapes.medium,
                    contentPadding = CompactButtonPadding,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = onCard,
                            contentColor = MaterialTheme.colorScheme.error,
                            disabledContainerColor = onCard.copy(alpha = 0.7f),
                            disabledContentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        if (isAcknowledging) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.active_alerts_acknowledge))
                    }
                }
                OutlinedButton(
                    onClick = onCallClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = CompactButtonPadding,
                    border = BorderStroke(1.dp, onCard.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = onCard)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_phone),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.active_alerts_call, careRecipientFirstName),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun subtitle(item: ActiveAlertItem, careRecipientFirstName: String): String {
    val detail = when {
        item.alert.status == AlertStatus.PENDING_CONFIRMATION ->
            stringResource(R.string.active_alerts_fall_waiting, careRecipientFirstName)
        item.alert.sourceType == AlertSourceType.FALL_DETECTED ->
            stringResource(R.string.active_alerts_fall_unanswered, careRecipientFirstName)
        item.alert.sourceType == AlertSourceType.SOS_TRIGGERED ->
            stringResource(R.string.active_alerts_sos_detail)
        else -> item.context.readingSummary
    }
    return listOfNotNull(detail, item.context.locationName).joinToString(" · ")
}

/** Countdown box: when the alert will reach the next contacts, or that it already reached everyone. */
@Composable
private fun EscalationStatus(
    modifier: Modifier = Modifier,
    alert: Alert,
    escalationSecondsLeft: Long?
) {
    val message = when {
        escalationSecondsLeft == null && alert.currentRecipientLevel == RecipientLevel.BROADCAST ->
            stringResource(R.string.active_alerts_sent_to_everyone)
        escalationSecondsLeft == null -> return
        escalationSecondsLeft == 0L -> stringResource(R.string.active_alerts_escalating)
        alert.currentRecipientLevel == RecipientLevel.PRIMARY ->
            stringResource(R.string.active_alerts_countdown_to_secondary)
        else -> stringResource(R.string.active_alerts_countdown_to_everyone)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onError.copy(alpha = 0.14f), MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        if (escalationSecondsLeft != null && escalationSecondsLeft > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatCountdown(escalationSecondsLeft),
                style = MaterialTheme.typography.dataMetric.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CriticalAlertCardPreview() {
    val now = Instant.parse("2026-10-03T14:33:05Z")
    GuardianTheme(dynamicColor = false) {
        CriticalAlertCard(
            modifier = Modifier.padding(16.dp),
            item = ActiveAlertItem(
                alert = Alert(
                    id = "1",
                    careRecipientProfileId = "elena",
                    sourceType = AlertSourceType.FALL_DETECTED,
                    severity = Severity.CRITICAL,
                    status = AlertStatus.TRIGGERED,
                    currentRecipientLevel = RecipientLevel.PRIMARY,
                    triggeredAt = now.minusSeconds(60),
                    lastDispatchedAt = now.minusSeconds(18)
                ),
                context = AlertContext(locationName = "Dormitorio"),
                awaitsMyAcknowledgement = true
            ),
            careRecipientFirstName = "Elena",
            now = now,
            escalationSecondsLeft = 42,
            isAcknowledging = false,
            onClick = {},
            onAcknowledgeClick = {},
            onCallClick = {}
        )
    }
}
