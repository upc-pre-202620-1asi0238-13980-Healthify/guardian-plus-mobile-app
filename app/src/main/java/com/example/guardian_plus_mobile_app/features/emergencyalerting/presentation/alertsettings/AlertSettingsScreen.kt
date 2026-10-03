package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertsettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettings
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.NotificationChannel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertsettings.component.AckTimeoutSelector
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertsettings.component.ContactsSummaryCard
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertsettings.component.SettingSwitchRow
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertsettings.component.SettingsSection
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.DetailTopBar

@Composable
fun AlertSettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: AlertSettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onContactsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Runs again when coming back from the contacts screen, so the summary is up to date
    LaunchedEffect(Unit) {
        viewModel.onScreenShown()
    }

    LaunchedEffect(uiState.actionErrorMessage) {
        uiState.actionErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onActionErrorShown()
        }
    }

    Box(modifier = modifier) {
        AlertSettingsContent(
            uiState = uiState,
            onBackClick = onBackClick,
            onRetryClick = viewModel::loadSettings,
            onContactsClick = onContactsClick,
            onEscalationChange = viewModel::setEscalationEnabled,
            onTimeoutSelect = viewModel::setAckTimeout,
            onBroadcastChange = viewModel::setBroadcastCriticalImmediately,
            onSilentModeChange = viewModel::setSilentModeEnabled,
            onChannelChange = viewModel::setChannelEnabled
        )
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun AlertSettingsContent(
    modifier: Modifier = Modifier,
    uiState: AlertSettingsUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onContactsClick: () -> Unit,
    onEscalationChange: (Boolean) -> Unit,
    onTimeoutSelect: (Int) -> Unit,
    onBroadcastChange: (Boolean) -> Unit,
    onSilentModeChange: (Boolean) -> Unit,
    onChannelChange: (NotificationChannel, Boolean) -> Unit
) {
    val settings = uiState.settings

    Column(modifier = modifier.fillMaxSize()) {
        DetailTopBar(
            title = stringResource(R.string.settings_title),
            subtitle = DemoSession.CARE_RECIPIENT_NAME,
            onBackClick = onBackClick
        )

        when {
            settings == null && uiState.errorMessage != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                OutlinedButton(onClick = onRetryClick, modifier = Modifier.padding(top = 12.dp), shape = MaterialTheme.shapes.medium) {
                    Text(text = stringResource(R.string.action_retry))
                }
            }

            settings == null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item(key = "contacts") {
                    ContactsSummaryCard(
                        contactCount = uiState.contacts.size,
                        primaryContactName = uiState.primaryContact?.displayName,
                        onClick = onContactsClick
                    )
                }
                item(key = "escalation") {
                    EscalationSection(
                        settings = settings,
                        enabled = !uiState.isSavingSettings,
                        onEscalationChange = onEscalationChange,
                        onTimeoutSelect = onTimeoutSelect,
                        onBroadcastChange = onBroadcastChange
                    )
                }
                item(key = "sound") {
                    SettingsSection(title = stringResource(R.string.settings_section_sound)) {
                        SettingSwitchRow(
                            title = stringResource(R.string.settings_silent_mode),
                            detail = stringResource(R.string.settings_silent_mode_detail, DemoSession.CARE_RECIPIENT_FIRST_NAME),
                            iconRes = R.drawable.ic_bell_off,
                            checked = settings.silentModeEnabled,
                            enabled = !uiState.isSavingSettings,
                            onCheckedChange = onSilentModeChange
                        )
                    }
                }
                item(key = "channels") {
                    ChannelsSection(uiState = uiState, onChannelChange = onChannelChange)
                }
            }
        }
    }
}

@Composable
private fun EscalationSection(
    modifier: Modifier = Modifier,
    settings: AlertSettings,
    enabled: Boolean,
    onEscalationChange: (Boolean) -> Unit,
    onTimeoutSelect: (Int) -> Unit,
    onBroadcastChange: (Boolean) -> Unit
) {
    SettingsSection(modifier = modifier, title = stringResource(R.string.settings_section_escalation)) {
        SettingSwitchRow(
            title = stringResource(R.string.settings_escalation_enabled),
            detail = stringResource(R.string.settings_escalation_enabled_detail),
            checked = settings.escalationEnabled,
            enabled = enabled,
            onCheckedChange = onEscalationChange
        )
        // The timeout only matters while escalation is on
        AckTimeoutSelector(
            selectedSeconds = settings.primaryAckTimeoutSec,
            enabled = enabled && settings.escalationEnabled,
            onSelect = onTimeoutSelect
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(top = 8.dp))
        SettingSwitchRow(
            title = stringResource(R.string.settings_broadcast_critical),
            detail = stringResource(R.string.settings_broadcast_critical_detail),
            checked = settings.broadcastCriticalImmediately,
            enabled = enabled,
            onCheckedChange = onBroadcastChange
        )
    }
}

@Composable
private fun ChannelsSection(
    modifier: Modifier = Modifier,
    uiState: AlertSettingsUiState,
    onChannelChange: (NotificationChannel, Boolean) -> Unit
) {
    val channels = listOf(
        Triple(NotificationChannel.PUSH, R.string.settings_channel_push, R.drawable.ic_smartphone),
        Triple(NotificationChannel.SMS, R.string.settings_channel_sms, R.drawable.ic_message_square),
        Triple(NotificationChannel.IN_APP, R.string.settings_channel_in_app, R.drawable.ic_bell)
    )
    Column(modifier = modifier) {
        SettingsSection(title = stringResource(R.string.settings_section_channels)) {
            channels.forEach { (channel, labelRes, iconRes) ->
                val checked = channel in uiState.enabledChannels
                SettingSwitchRow(
                    title = stringResource(labelRes),
                    iconRes = iconRes,
                    checked = checked,
                    // The last channel keeps its switch active so it reads as "on"; turning it off explains why it cannot
                    enabled = uiState.savingChannel == null,
                    onCheckedChange = { enabled -> onChannelChange(channel, enabled) }
                )
            }
        }
        Text(
            text = stringResource(R.string.settings_channels_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun AlertSettingsContentPreview() {
    GuardianTheme(dynamicColor = false) {
        PreviewContent(
            AlertSettingsUiState(
                settings = AlertSettings(
                    careRecipientProfileId = "elena",
                    primaryAckTimeoutSec = 60,
                    escalationEnabled = true,
                    silentModeEnabled = false,
                    broadcastCriticalImmediately = false
                ),
                contacts = listOf(
                    EmergencyContact("c1", "elena", "maria", "María Rojas", "Hija", "+51987654321", 1, true),
                    EmergencyContact("c2", "elena", "carlos", "Carlos Rojas", "Hijo", "+51987654322", 2, true)
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertSettingsContentLoadingPreview() {
    GuardianTheme(dynamicColor = false) {
        PreviewContent(AlertSettingsUiState(isLoading = true))
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertSettingsContentErrorPreview() {
    GuardianTheme(dynamicColor = false) {
        PreviewContent(AlertSettingsUiState(errorMessage = "No se pudo conectar con el servidor. Revisa tu conexión."))
    }
}

@Composable
private fun PreviewContent(uiState: AlertSettingsUiState) {
    AlertSettingsContent(
        uiState = uiState,
        onBackClick = {},
        onRetryClick = {},
        onContactsClick = {},
        onEscalationChange = {},
        onTimeoutSelect = {},
        onBroadcastChange = {},
        onSilentModeChange = {},
        onChannelChange = { _, _ -> }
    )
}
