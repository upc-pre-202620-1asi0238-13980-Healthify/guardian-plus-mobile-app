package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.component.AlertItem
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.component.CriticalAlertCard
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.component.EscalationNotice
import java.time.Instant

@Composable
fun ActiveAlertsScreen(
    modifier: Modifier = Modifier,
    viewModel: ActiveAlertsViewModel = hiltViewModel(),
    onAlertClick: (String) -> Unit,
    onCallClick: () -> Unit,
    onManageContactsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // After "Reconocer" the member goes straight to the incident in attention
    LaunchedEffect(uiState.acknowledgedAlertId) {
        uiState.acknowledgedAlertId?.let { alertId ->
            viewModel.onAcknowledgedAlertOpened()
            onAlertClick(alertId)
        }
    }

    // A rejected acknowledgement (e.g. someone else already did it) is reported once
    LaunchedEffect(uiState.actionErrorMessage) {
        uiState.actionErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onActionErrorShown()
        }
    }

    Box(modifier = modifier) {
        ActiveAlertsContent(
            uiState = uiState,
            onAlertClick = onAlertClick,
            onAcknowledgeClick = viewModel::acknowledge,
            onCallClick = onCallClick,
            onRetryClick = viewModel::loadAlerts,
            onManageContactsClick = onManageContactsClick
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ActiveAlertsContent(
    modifier: Modifier = Modifier,
    uiState: ActiveAlertsUiState,
    onAlertClick: (String) -> Unit,
    onAcknowledgeClick: (String) -> Unit,
    onCallClick: () -> Unit,
    onRetryClick: () -> Unit,
    onManageContactsClick: () -> Unit
) {
    val hasAlerts = uiState.activeCount > 0

    when {
        uiState.isLoading && !hasAlerts -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        uiState.errorMessage != null && !hasAlerts -> ErrorState(
            modifier = modifier,
            message = uiState.errorMessage,
            onRetryClick = onRetryClick
        )

        !hasAlerts -> EmptyState(modifier = modifier, careRecipientFirstName = uiState.careRecipientFirstName)

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.featuredAlert?.let { featured ->
                item(key = featured.alert.id) {
                    CriticalAlertCard(
                        item = featured,
                        careRecipientFirstName = uiState.careRecipientFirstName,
                        now = uiState.now,
                        escalationSecondsLeft = uiState.escalationSecondsLeft,
                        isAcknowledging = uiState.acknowledgingAlertId == featured.alert.id,
                        onClick = { onAlertClick(featured.alert.id) },
                        onAcknowledgeClick = { onAcknowledgeClick(featured.alert.id) },
                        onCallClick = onCallClick
                    )
                }
            }
            if (uiState.otherAlerts.isNotEmpty()) {
                item(key = "others-title") {
                    Text(
                        text = stringResource(
                            if (uiState.featuredAlert != null) R.string.active_alerts_others else R.string.active_alerts_all
                        ),
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(uiState.otherAlerts, key = { it.alert.id }) { item ->
                    AlertItem(
                        item = item,
                        careRecipientFirstName = uiState.careRecipientFirstName,
                        now = uiState.now,
                        onClick = { onAlertClick(item.alert.id) }
                    )
                }
            }
            if (uiState.escalationEnabled) {
                item(key = "escalation-notice") {
                    EscalationNotice(ackTimeoutSec = uiState.ackTimeoutSec, onManageContactsClick = onManageContactsClick)
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier, careRecipientFirstName: String) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }
        Text(
            text = stringResource(R.string.active_alerts_empty_title, careRecipientFirstName),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.active_alerts_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
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
                OutlinedButton(
                    onClick = onRetryClick,
                    modifier = Modifier.padding(top = 12.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = stringResource(R.string.action_retry))
                }
            }
        }
    }
}

private val previewNow: Instant = Instant.parse("2026-10-03T14:33:05Z")

private val previewState = ActiveAlertsUiState(
    careRecipientFirstName = "Elena",
    featuredAlert = ActiveAlertItem(
        alert = Alert(
            id = "1",
            careRecipientProfileId = "elena",
            sourceType = AlertSourceType.FALL_DETECTED,
            severity = Severity.CRITICAL,
            status = AlertStatus.TRIGGERED,
            currentRecipientLevel = RecipientLevel.PRIMARY,
            triggeredAt = previewNow.minusSeconds(60),
            lastDispatchedAt = previewNow.minusSeconds(18)
        ),
        context = AlertContext(locationName = "Dormitorio"),
        awaitsMyAcknowledgement = true
    ),
    otherAlerts = listOf(
        ActiveAlertItem(
            alert = Alert(
                id = "2",
                careRecipientProfileId = "elena",
                sourceType = AlertSourceType.VITAL_SIGN_ANOMALY,
                severity = Severity.HIGH,
                status = AlertStatus.TRIGGERED,
                currentRecipientLevel = RecipientLevel.PRIMARY,
                triggeredAt = previewNow.minusSeconds(360)
            ),
            context = AlertContext(readingSummary = "124 lpm · 3 lecturas fuera de rango")
        ),
        ActiveAlertItem(
            alert = Alert(
                id = "3",
                careRecipientProfileId = "elena",
                sourceType = AlertSourceType.MEDICATION_RESTOCK_SUGGESTED,
                severity = Severity.MEDIUM,
                status = AlertStatus.ACKNOWLEDGED,
                currentRecipientLevel = RecipientLevel.PRIMARY,
                triggeredAt = previewNow.minusSeconds(1_500)
            ),
            context = AlertContext(readingSummary = "Losartán 50 mg · quedan 2 días")
        )
    ),
    now = previewNow
)

@Preview(showBackground = true, heightDp = 760)
@Composable
private fun ActiveAlertsContentPreview() {
    GuardianTheme(dynamicColor = false) {
        ActiveAlertsContent(
            uiState = previewState,
            onAlertClick = {},
            onAcknowledgeClick = {},
            onCallClick = {},
            onRetryClick = {},
            onManageContactsClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ActiveAlertsContentLoadingPreview() {
    GuardianTheme(dynamicColor = false) {
        ActiveAlertsContent(
            uiState = ActiveAlertsUiState(isLoading = true),
            onAlertClick = {},
            onAcknowledgeClick = {},
            onCallClick = {},
            onRetryClick = {},
            onManageContactsClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ActiveAlertsContentErrorPreview() {
    GuardianTheme(dynamicColor = false) {
        ActiveAlertsContent(
            uiState = ActiveAlertsUiState(errorMessage = "No se pudo conectar con el servidor. Revisa tu conexión."),
            onAlertClick = {},
            onAcknowledgeClick = {},
            onCallClick = {},
            onRetryClick = {},
            onManageContactsClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ActiveAlertsContentEmptyPreview() {
    GuardianTheme(dynamicColor = false) {
        ActiveAlertsContent(
            uiState = ActiveAlertsUiState(careRecipientFirstName = "Elena"),
            onAlertClick = {},
            onAcknowledgeClick = {},
            onCallClick = {},
            onRetryClick = {},
            onManageContactsClick = {}
        )
    }
}
