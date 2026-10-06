package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.health

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.text.initials
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.LiveVitalsUiState
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.previewLiveVitals
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.health.component.HealthHeader
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.health.component.HealthTab
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.health.component.HealthTabRow
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.LiveVitalsScreen
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.LiveVitalsViewModel
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.VitalHistoryScreen

/** Health tab of the bottom bar: shared header and the "Ahora · Historial" tabs. */
@Composable
fun HealthScreen(modifier: Modifier = Modifier) {
    // Same instance that LiveVitalsScreen uses, since both live in this navigation entry
    val liveVitalsViewModel: LiveVitalsViewModel = hiltViewModel()
    val liveState by liveVitalsViewModel.uiState.collectAsStateWithLifecycle()
    // A pure UI choice, so it survives rotation with rememberSaveable instead of living in a ViewModel
    var selectedTab by rememberSaveable { mutableStateOf(HealthTab.NOW) }

    HealthContent(
        modifier = modifier,
        liveState = liveState,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it }
    ) {
        when (selectedTab) {
            HealthTab.NOW -> LiveVitalsScreen(viewModel = liveVitalsViewModel)
            HealthTab.HISTORY -> VitalHistoryScreen(onSeeAllClick = { selectedTab = HealthTab.NOW })
        }
    }
}

@Composable
fun HealthContent(
    modifier: Modifier = Modifier,
    liveState: LiveVitalsUiState,
    selectedTab: HealthTab,
    onTabSelected: (HealthTab) -> Unit,
    tabContent: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        HealthHeader(
            userInitials = DemoSession.CURRENT_USER_NAME.initials(),
            careRecipientName = DemoSession.CARE_RECIPIENT_NAME,
            hasLiveSignal = liveState.hasLiveSignal,
            allWithinRange = liveState.allWithinRange,
            hasReadings = !liveState.vitals?.vitalSigns.isNullOrEmpty()
        )
        HealthTabRow(
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
private fun HealthContentPreview() {
    GuardianTheme(dynamicColor = false) {
        HealthContent(
            liveState = LiveVitalsUiState(vitals = previewLiveVitals()),
            selectedTab = HealthTab.HISTORY,
            onTabSelected = {}
        ) {}
    }
}
