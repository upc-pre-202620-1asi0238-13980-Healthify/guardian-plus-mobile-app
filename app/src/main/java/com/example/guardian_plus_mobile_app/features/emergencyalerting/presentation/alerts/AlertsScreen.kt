package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.ActiveAlertsScreen
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.ActiveAlertsViewModel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerts.component.AlertsHeader
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerts.component.AlertsTab
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerts.component.AlertsTabRow
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.dial

/** Alerts tab of the bottom bar: shared header and the "Activas · Historial" tabs. */
@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier,
    onAlertClick: (String) -> Unit
) {
    // Same instance that ActiveAlertsScreen uses, since both live in this navigation entry
    val activeAlertsViewModel: ActiveAlertsViewModel = hiltViewModel()
    val activeAlertsState by activeAlertsViewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(AlertsTab.ACTIVE) }
    val context = LocalContext.current

    AlertsContent(
        modifier = modifier,
        activeCount = activeAlertsState.activeCount,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it }
    ) {
        when (selectedTab) {
            AlertsTab.ACTIVE -> ActiveAlertsScreen(
                viewModel = activeAlertsViewModel,
                onAlertClick = onAlertClick,
                onCallClick = { context.dial(DemoSession.CARE_RECIPIENT_PHONE) }
            )
            AlertsTab.HISTORY -> HistoryPlaceholder()
        }
    }
}

@Composable
fun AlertsContent(
    modifier: Modifier = Modifier,
    activeCount: Int,
    selectedTab: AlertsTab,
    onTabSelected: (AlertsTab) -> Unit,
    tabContent: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        AlertsHeader(
            userInitials = DemoSession.CURRENT_USER_NAME.initials(),
            careRecipientName = DemoSession.CARE_RECIPIENT_NAME,
            activeCount = activeCount
        )
        AlertsTabRow(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp),
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )
        Box(modifier = Modifier.weight(1f)) {
            tabContent()
        }
    }
}

@Composable
private fun HistoryPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.alerts_history_coming_soon),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** "María Rojas" → "MR". */
private fun String.initials(): String =
    split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

@Preview(showBackground = true)
@Composable
private fun AlertsContentPreview() {
    GuardianTheme(dynamicColor = false) {
        AlertsContent(activeCount = 3, selectedTab = AlertsTab.HISTORY, onTabSelected = {}) {
            HistoryPlaceholder()
        }
    }
}
