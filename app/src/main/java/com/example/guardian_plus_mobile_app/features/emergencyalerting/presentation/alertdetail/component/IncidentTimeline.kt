package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.FallConfirmationWindow
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.TimelineEvent
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.title
import java.time.Instant

/** "Seguimiento del incidente": each recorded step with its exact time, plus the next escalation if any. */
@Composable
fun IncidentTimeline(
    modifier: Modifier = Modifier,
    events: List<TimelineEvent>,
    sourceType: AlertSourceType,
    careRecipientFirstName: String
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.detail_timeline_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            events.forEach { event ->
                TimelineRow(
                    event = event,
                    text = event.describe(sourceType, careRecipientFirstName)
                )
            }
        }
    }
}

@Composable
private fun TimelineRow(modifier: Modifier = Modifier, event: TimelineEvent, text: String) {
    val pending = event is TimelineEvent.NextEscalation
    val alarming = event is TimelineEvent.Detected || event is TimelineEvent.Confirmed
    val dotColor = if (alarming) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(10.dp)
                .then(
                    if (pending) {
                        Modifier.border(1.5.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
                    } else {
                        Modifier.background(dotColor, CircleShape)
                    }
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = formatClockTime(event.at, withSeconds = true),
            style = MaterialTheme.typography.dataLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (pending) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TimelineEvent.describe(sourceType: AlertSourceType, careRecipientFirstName: String): String {
    val someone = stringResource(R.string.unknown_member)
    return when (this) {
        is TimelineEvent.Detected -> when (sourceType) {
            AlertSourceType.FALL_DETECTED -> stringResource(R.string.timeline_detected_fall)
            AlertSourceType.SOS_TRIGGERED -> stringResource(R.string.timeline_detected_sos, careRecipientFirstName)
            else -> stringResource(R.string.timeline_detected_other, sourceType.title(careRecipientFirstName))
        }
        is TimelineEvent.Confirmed ->
            stringResource(R.string.timeline_confirmed, careRecipientFirstName, FallConfirmationWindow.SECONDS.toInt())
        is TimelineEvent.Dismissed -> stringResource(R.string.timeline_dismissed, careRecipientFirstName)
        is TimelineEvent.SentTo -> stringResource(
            R.string.timeline_sent_to,
            names.ifEmpty { listOf(someone) }.joinToString(", ")
        )
        is TimelineEvent.Acknowledged -> stringResource(R.string.timeline_acknowledged, name ?: someone)
        is TimelineEvent.OnTheWay -> stringResource(R.string.timeline_on_the_way, name ?: someone)
        is TimelineEvent.Arrived -> stringResource(R.string.timeline_arrived, name ?: someone, careRecipientFirstName)
        is TimelineEvent.Stabilized -> stringResource(R.string.timeline_stabilized)
        is TimelineEvent.Closed -> stringResource(R.string.timeline_closed)
        is TimelineEvent.NextEscalation -> stringResource(R.string.timeline_next_escalation)
    }
}

@Preview(showBackground = true)
@Composable
private fun IncidentTimelinePreview() {
    val start = Instant.parse("2026-10-03T19:32:05Z")
    GuardianTheme(dynamicColor = false) {
        IncidentTimeline(
            modifier = Modifier.padding(16.dp),
            events = listOf(
                TimelineEvent.Detected(start),
                TimelineEvent.Confirmed(start.plusSeconds(20)),
                TimelineEvent.SentTo(start.plusSeconds(22), listOf("María Rojas")),
                TimelineEvent.NextEscalation(start.plusSeconds(82))
            ),
            sourceType = AlertSourceType.FALL_DETECTED,
            careRecipientFirstName = "Elena"
        )
    }
}
