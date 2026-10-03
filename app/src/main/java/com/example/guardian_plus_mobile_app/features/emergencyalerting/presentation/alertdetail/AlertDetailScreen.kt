package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertDelivery
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.DeliveryStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Incident
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.NotificationChannel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component.AlertDetailActions
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component.AlertDetailTopBar
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component.AlertHeroCard
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component.AvailableActions
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component.DeviceCard
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component.EscalationCard
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component.IncidentSummaryCard
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component.IncidentTimeline
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component.LocationCard
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component.NotesDialog
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component.VitalsRow
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.colors
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.dial
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.labelRes
import java.time.Instant

private const val EMERGENCY_SERVICES_NUMBER = "106"

@Composable
fun AlertDetailScreen(
    modifier: Modifier = Modifier,
    alertId: String,
    viewModel: AlertDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onViewLocationClick: () -> Unit,
    onViewHealthClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    // Which confirmation dialog is open (stabilize or close); purely visual, so it stays in the UI
    var notesDialogAction by rememberSaveable { mutableStateOf<AlertDetailAction?>(null) }

    LaunchedEffect(alertId) {
        viewModel.load(alertId)
    }

    LaunchedEffect(uiState.actionErrorMessage) {
        uiState.actionErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onActionErrorShown()
        }
    }

    Box(modifier = modifier) {
        AlertDetailContent(
            uiState = uiState,
            notesDialogAction = notesDialogAction,
            onBackClick = onBackClick,
            onRetryClick = { viewModel.load(alertId) },
            onAcknowledgeClick = viewModel::acknowledge,
            onClaimClick = viewModel::claimResponse,
            onCompleteClick = viewModel::completeResponse,
            onStabilizeClick = { notesDialogAction = AlertDetailAction.STABILIZE },
            onCloseClick = { notesDialogAction = AlertDetailAction.CLOSE },
            onNotesConfirm = { action, notes ->
                notesDialogAction = null
                if (action == AlertDetailAction.STABILIZE) viewModel.stabilize(notes) else viewModel.close(notes)
            },
            onNotesDismiss = { notesDialogAction = null },
            onCallClick = { context.dial(DemoSession.CARE_RECIPIENT_PHONE) },
            onCallEmergencyClick = { context.dial(EMERGENCY_SERVICES_NUMBER) },
            onViewLocationClick = onViewLocationClick,
            onViewHealthClick = onViewHealthClick
        )
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun AlertDetailContent(
    modifier: Modifier = Modifier,
    uiState: AlertDetailUiState,
    notesDialogAction: AlertDetailAction?,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onAcknowledgeClick: () -> Unit,
    onClaimClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onStabilizeClick: () -> Unit,
    onCloseClick: () -> Unit,
    onNotesConfirm: (AlertDetailAction, String) -> Unit,
    onNotesDismiss: () -> Unit,
    onCallClick: () -> Unit,
    onCallEmergencyClick: () -> Unit,
    onViewLocationClick: () -> Unit,
    onViewHealthClick: () -> Unit
) {
    val alert = uiState.alert
    val (statusLabel, statusColors) = statusChip(alert, uiState.incident)

    Column(modifier = modifier.fillMaxSize()) {
        AlertDetailTopBar(
            title = stringResource(
                if (alert?.sourceType == AlertSourceType.SOS_TRIGGERED) R.string.detail_title_sos else R.string.detail_title
            ),
            subtitle = DemoSession.CARE_RECIPIENT_NAME,
            statusLabel = statusLabel,
            statusContainer = statusColors.first,
            statusContent = statusColors.second,
            onBackClick = onBackClick
        )

        when {
            alert == null && uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage,
                onRetryClick = onRetryClick
            )

            alert == null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            else -> DetailBody(
                uiState = uiState,
                alert = alert,
                onAcknowledgeClick = onAcknowledgeClick,
                onClaimClick = onClaimClick,
                onCompleteClick = onCompleteClick,
                onStabilizeClick = onStabilizeClick,
                onCloseClick = onCloseClick,
                onCallClick = onCallClick,
                onCallEmergencyClick = onCallEmergencyClick,
                onViewLocationClick = onViewLocationClick,
                onViewHealthClick = onViewHealthClick
            )
        }
    }

    notesDialogAction?.let { action ->
        val stabilizing = action == AlertDetailAction.STABILIZE
        NotesDialog(
            title = stringResource(if (stabilizing) R.string.notes_dialog_stabilize_title else R.string.notes_dialog_close_title),
            message = if (stabilizing) {
                stringResource(R.string.notes_dialog_stabilize_message, uiState.careRecipientFirstName)
            } else {
                stringResource(R.string.notes_dialog_close_message)
            },
            confirmLabel = stringResource(if (stabilizing) R.string.action_stabilize else R.string.action_close_incident),
            onConfirm = { notes -> onNotesConfirm(action, notes) },
            onDismiss = onNotesDismiss
        )
    }
}

@Composable
private fun DetailBody(
    modifier: Modifier = Modifier,
    uiState: AlertDetailUiState,
    alert: Alert,
    onAcknowledgeClick: () -> Unit,
    onClaimClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onStabilizeClick: () -> Unit,
    onCloseClick: () -> Unit,
    onCallClick: () -> Unit,
    onCallEmergencyClick: () -> Unit,
    onViewLocationClick: () -> Unit,
    onViewHealthClick: () -> Unit
) {
    val incident = uiState.incident
    val context = uiState.context
    val calm = incident?.status == IncidentStatus.STABILIZED || uiState.isFinished
    val showContacts = uiState.escalationContacts.isNotEmpty() &&
        alert.status != AlertStatus.PENDING_CONFIRMATION &&
        alert.status != AlertStatus.DISMISSED
    val actions = AvailableActions(
        canAcknowledge = uiState.canAcknowledge,
        canClaim = uiState.canClaim,
        canComplete = uiState.myActiveResponseId != null,
        canStabilize = uiState.canStabilize,
        canClose = uiState.canClose,
        canCall = !uiState.isFinished,
        canCallEmergency = alert.sourceType == AlertSourceType.SOS_TRIGGERED && !uiState.isFinished,
        canViewHealth = alert.sourceType == AlertSourceType.VITAL_SIGN_ANOMALY && calm
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "hero") {
            AlertHeroCard(
                alert = alert,
                incident = incident,
                context = context,
                careRecipientFirstName = uiState.careRecipientFirstName,
                confirmationSecondsLeft = uiState.confirmationSecondsLeft,
                now = uiState.now
            )
        }
        if (calm && incident != null) {
            item(key = "summary") {
                IncidentSummaryCard(
                    alert = alert,
                    incident = incident,
                    acknowledgedByName = uiState.contactName(alert.acknowledgedByUserId),
                    careRecipientFirstName = uiState.careRecipientFirstName
                )
            }
        }
        if (showContacts && !calm) {
            item(key = "contacts") {
                EscalationCard(
                    contacts = uiState.escalationContacts,
                    escalationSecondsLeft = uiState.escalationSecondsLeft,
                    ackTimeoutSec = uiState.ackTimeoutSec,
                    currentRecipientLevel = alert.currentRecipientLevel
                )
            }
        }
        item(key = "timeline") {
            IncidentTimeline(
                events = uiState.timeline,
                sourceType = alert.sourceType,
                careRecipientFirstName = uiState.careRecipientFirstName
            )
        }
        if (!calm && (context.heartRateBpm != null || context.oxygenSaturation != null)) {
            item(key = "vitals") {
                VitalsRow(heartRateBpm = context.heartRateBpm, oxygenSaturation = context.oxygenSaturation)
            }
        }
        if (!calm && context.locationName != null) {
            item(key = "location") {
                LocationCard(
                    locationName = context.locationName,
                    updatedAgo = context.locationUpdatedAgo,
                    onViewMapClick = onViewLocationClick
                )
            }
        }
        if (!calm && context.deviceSummary != null) {
            item(key = "device") {
                DeviceCard(deviceSummary = context.deviceSummary)
            }
        }
        item(key = "actions") {
            AlertDetailActions(
                actions = actions,
                runningAction = uiState.runningAction,
                careRecipientFirstName = uiState.careRecipientFirstName,
                onAcknowledgeClick = onAcknowledgeClick,
                onClaimClick = onClaimClick,
                onCompleteClick = onCompleteClick,
                onStabilizeClick = onStabilizeClick,
                onCloseClick = onCloseClick,
                onCallClick = onCallClick,
                onCallEmergencyClick = onCallEmergencyClick,
                onViewHealthClick = onViewHealthClick
            )
        }
    }
}

/** Label and colors of the chip at the top right: the incident's state once there is one, else the alert's. */
@Composable
private fun statusChip(alert: Alert?, incident: Incident?): Pair<String?, Pair<Color, Color>> = when {
    incident != null -> stringResource(incident.status.labelRes()) to incident.status.colors()
    alert != null -> stringResource(alert.status.labelRes()) to alert.status.colors()
    else -> null to (MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun ErrorState(modifier: Modifier = Modifier, message: String, onRetryClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                OutlinedButton(onClick = onRetryClick, modifier = Modifier.padding(top = 12.dp), shape = MaterialTheme.shapes.medium) {
                    Text(text = stringResource(R.string.action_retry))
                }
            }
        }
    }
}

// Previews: one per state, with the same data the Figma frames show

private val previewNow: Instant = Instant.parse("2026-10-03T19:33:17Z")
private val previewContacts = listOf(
    EmergencyContact("c1", "elena", "maria", "María Rojas", "Hija", "+51987654321", 1, true),
    EmergencyContact("c2", "elena", "carlos", "Carlos Rojas", "Hijo", "+51987654322", 2, true)
)

private fun previewDelivery(userId: String, level: RecipientLevel, sentAt: Instant) = AlertDelivery(
    id = "d-$userId",
    recipientUserId = userId,
    recipientLevel = level,
    channel = NotificationChannel.IN_APP,
    status = DeliveryStatus.SENT,
    sentAt = sentAt,
    deliveredAt = null
)

private val previewFallState = AlertDetailUiState(
    alert = Alert(
        id = "1",
        careRecipientProfileId = "elena",
        sourceType = AlertSourceType.FALL_DETECTED,
        severity = Severity.CRITICAL,
        status = AlertStatus.TRIGGERED,
        currentRecipientLevel = RecipientLevel.PRIMARY,
        triggeredAt = previewNow.minusSeconds(72),
        confirmedAt = previewNow.minusSeconds(52),
        lastDispatchedAt = previewNow.minusSeconds(18),
        deliveries = listOf(previewDelivery("maria", RecipientLevel.PRIMARY, previewNow.minusSeconds(18)))
    ),
    context = AlertContext(locationName = "Dormitorio · Casa de Elena", locationUpdatedAgo = "hace 30 s", heartRateBpm = 112, oxygenSaturation = 95),
    contacts = previewContacts,
    careRecipientFirstName = "Elena",
    currentUserId = "maria",
    now = previewNow
)

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun AlertDetailFallPreview() {
    GuardianTheme(dynamicColor = false) {
        PreviewContent(uiState = previewFallState)
    }
}

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun AlertDetailSosPreview() {
    val sent = previewNow.minusSeconds(20)
    GuardianTheme(dynamicColor = false) {
        PreviewContent(
            uiState = previewFallState.copy(
                alert = previewFallState.alert!!.copy(
                    sourceType = AlertSourceType.SOS_TRIGGERED,
                    currentRecipientLevel = RecipientLevel.BROADCAST,
                    triggeredAt = sent,
                    confirmedAt = sent,
                    lastDispatchedAt = sent,
                    deliveries = previewContacts.map { previewDelivery(it.userId, RecipientLevel.BROADCAST, sent) }
                ),
                context = AlertContext(
                    locationName = "Parque Kennedy · Miraflores",
                    locationUpdatedAgo = "hace 10 s",
                    deviceSummary = "Conectada · Batería 64%"
                )
            )
        )
    }
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun AlertDetailStabilizedPreview() {
    val start = previewNow.minusSeconds(1_100)
    GuardianTheme(dynamicColor = false) {
        PreviewContent(
            uiState = previewFallState.copy(
                alert = previewFallState.alert!!.copy(
                    sourceType = AlertSourceType.VITAL_SIGN_ANOMALY,
                    severity = Severity.HIGH,
                    status = AlertStatus.ACKNOWLEDGED,
                    triggeredAt = start,
                    confirmedAt = start,
                    lastDispatchedAt = start,
                    acknowledgedAt = start.plusSeconds(48),
                    acknowledgedByUserId = "maria",
                    deliveries = listOf(previewDelivery("maria", RecipientLevel.PRIMARY, start))
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
                context = AlertContext(readingSummary = "128 → 84 lpm")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertDetailLoadingPreview() {
    GuardianTheme(dynamicColor = false) {
        PreviewContent(uiState = AlertDetailUiState(isLoading = true))
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertDetailErrorPreview() {
    GuardianTheme(dynamicColor = false) {
        PreviewContent(uiState = AlertDetailUiState(errorMessage = "No se pudo conectar con el servidor. Revisa tu conexión."))
    }
}

@Composable
private fun PreviewContent(uiState: AlertDetailUiState) {
    AlertDetailContent(
        uiState = uiState,
        notesDialogAction = null,
        onBackClick = {},
        onRetryClick = {},
        onAcknowledgeClick = {},
        onClaimClick = {},
        onCompleteClick = {},
        onStabilizeClick = {},
        onCloseClick = {},
        onNotesConfirm = { _, _ -> },
        onNotesDismiss = {},
        onCallClick = {},
        onCallEmergencyClick = {},
        onViewLocationClick = {},
        onViewHealthClick = {}
    )
}
