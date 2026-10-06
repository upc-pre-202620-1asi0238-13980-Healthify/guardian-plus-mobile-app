package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.text.initials
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.ActiveAlertsScreen
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.ActiveAlertsViewModel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory.AlertHistoryScreen
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerts.component.AlertsHeader
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerts.component.AlertsTab
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerts.component.AlertsTabRow
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.dial

/** Alerts tab of the bottom bar: shared header and the "Activas · Historial" tabs. */
@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier,
    onAlertClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onManageContactsClick: () -> Unit
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
        onTabSelected = { selectedTab = it },
        onSettingsClick = onSettingsClick
    ) {
        when (selectedTab) {
            AlertsTab.ACTIVE -> ActiveAlertsScreen(
                viewModel = activeAlertsViewModel,
                onAlertClick = onAlertClick,
                onCallClick = { context.dial(DemoSession.CARE_RECIPIENT_PHONE) },
                onManageContactsClick = onManageContactsClick
            )
            AlertsTab.HISTORY -> AlertHistoryScreen(onAlertClick = onAlertClick)
        }
    }
}

@Composable
fun AlertsContent(
    modifier: Modifier = Modifier,
    activeCount: Int,
    selectedTab: AlertsTab,
    onTabSelected: (AlertsTab) -> Unit,
    onSettingsClick: () -> Unit,
    tabContent: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        AlertsHeader(
            userInitials = DemoSession.CURRENT_USER_NAME.initials(),
            careRecipientName = DemoSession.CARE_RECIPIENT_NAME,
            activeCount = activeCount,
            onSettingsClick = onSettingsClick
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

@Preview(showBackground = true)
@Composable
private fun AlertsContentPreview() {
    GuardianTheme(dynamicColor = false) {
        AlertsContent(activeCount = 3, selectedTab = AlertsTab.ACTIVE, onTabSelected = {}, onSettingsClick = {}) {}
    }
}
