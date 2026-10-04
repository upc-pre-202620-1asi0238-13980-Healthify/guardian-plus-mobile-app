package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.ContactStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.EscalationContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.StatusChip
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatCountdown

/**
 * Who the alert reached and what each contact did. While it waits for an acknowledgement it also
 * shows the countdown to the next escalation level (US11); a broadcast lists everyone at once (US25).
 */
@Composable
fun EscalationCard(
    modifier: Modifier = Modifier,
    contacts: List<EscalationContact>,
    escalationSecondsLeft: Long?,
    ackTimeoutSec: Int,
    currentRecipientLevel: RecipientLevel?
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(
                        if (escalationSecondsLeft != null) R.string.detail_escalation_title else R.string.detail_contacts_title
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (escalationSecondsLeft != null) {
                    Text(
                        text = formatCountdown(escalationSecondsLeft),
                        style = MaterialTheme.typography.dataMetric.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Text(
                text = stringResource(
                    when {
                        escalationSecondsLeft == null -> R.string.detail_contacts_description
                        currentRecipientLevel == RecipientLevel.PRIMARY -> R.string.detail_escalation_description
                        else -> R.string.detail_escalation_description_everyone
                    }
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (escalationSecondsLeft != null && ackTimeoutSec > 0) {
                LinearProgressIndicator(
                    progress = { 1f - escalationSecondsLeft.toFloat() / ackTimeoutSec },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.error,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round,
                    gapSize = 0.dp,
                    drawStopIndicator = {}
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            contacts.forEach { item ->
                ContactRow(item = item)
            }
        }
    }
}

@Composable
private fun ContactRow(modifier: Modifier = Modifier, item: EscalationContact) {
    val contact = item.contact
    val isPrimary = contact.priorityOrder == 1
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    if (isPrimary) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.displayName.split(" ").filter { it.isNotBlank() }.take(2)
                    .joinToString("") { it.first().uppercase() },
                style = MaterialTheme.typography.labelMedium,
                color = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.displayName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(
                    if (isPrimary) R.string.detail_contact_role_primary else R.string.detail_contact_role_secondary,
                    contact.relationship
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ContactStatusChip(status = item.status)
    }
}

@Composable
private fun ContactStatusChip(modifier: Modifier = Modifier, status: ContactStatus) {
    val (label, positive) = when (status) {
        ContactStatus.WAITING -> R.string.contact_status_waiting to false
        ContactStatus.SENDING -> R.string.contact_status_sending to false
        ContactStatus.SENT -> R.string.contact_status_sent to true
        ContactStatus.DELIVERED -> R.string.contact_status_delivered to true
        ContactStatus.FAILED -> R.string.contact_status_failed to false
        ContactStatus.ACKNOWLEDGED -> R.string.contact_status_acknowledged to true
        ContactStatus.ON_THE_WAY -> R.string.contact_status_on_the_way to true
        ContactStatus.ARRIVED -> R.string.contact_status_arrived to true
        ContactStatus.NOT_NEEDED -> R.string.contact_status_not_needed to false
    }
    val (container, content) = when {
        status == ContactStatus.FAILED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        positive -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    StatusChip(
        modifier = modifier,
        text = stringResource(label),
        container = container,
        content = content,
        showCheck = positive
    )
}

@Preview(showBackground = true)
@Composable
private fun EscalationCardPreview() {
    GuardianTheme(dynamicColor = false) {
        EscalationCard(
            modifier = Modifier.padding(16.dp),
            contacts = listOf(
                EscalationContact(
                    EmergencyContact("1", "elena", "maria", "María Rojas", "Hija", "+51987654321", 1, true),
                    ContactStatus.SENT
                ),
                EscalationContact(
                    EmergencyContact("2", "elena", "carlos", "Carlos Rojas", "Hijo", "+51987654322", 2, true),
                    ContactStatus.WAITING
                )
            ),
            escalationSecondsLeft = 42,
            ackTimeoutSec = 60,
            currentRecipientLevel = RecipientLevel.PRIMARY
        )
    }
}
