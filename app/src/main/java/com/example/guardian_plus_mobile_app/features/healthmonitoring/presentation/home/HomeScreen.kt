package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.session.LocalCareContext
import com.example.guardian_plus_mobile_app.core.session.callCareRecipient
import com.example.guardian_plus_mobile_app.core.text.initials
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.relativeTime
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.ErrorState
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.LiveVitalsUiState
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.previewLiveVitals
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.previewNow
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.component.CareRecipientCard
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.component.HomeHeader
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.component.QuickActionsRow
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.component.SectionTitle
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.component.StatusHeroCard
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.component.UpcomingCard
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.component.VitalsSummaryCard
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.LiveVitalsViewModel
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.wearable.LinkWearableSheet
import java.time.ZoneId

/** "Inicio" tab: how the care recipient is doing right now. Only reads live vitals, so no ViewModel of its own. */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: LiveVitalsViewModel = hiltViewModel(),
    onSeeVitalsClick: () -> Unit,
    onLocationClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onProfileClick: () -> Unit = {}
) {
    val careContext = LocalCareContext.current
    var isLinkWearableOpen by rememberSaveable { mutableStateOf(false) }
    val linkedMessage = stringResource(R.string.wearable_linked)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val comingSoon = stringResource(R.string.placeholder_soon)
    val showComingSoon = { Toast.makeText(context, comingSoon, Toast.LENGTH_SHORT).show() }

    HomeContent(
        modifier = modifier,
        uiState = uiState,
        onRetryClick = viewModel::load,
        onBellClick = onAlertsClick,
        onChangeRecipientClick = showComingSoon,
        onCallClick = { context.callCareRecipient(careContext) },
        onVideoClick = showComingSoon,
        onLocationClick = onLocationClick,
        onSeeVitalsClick = onSeeVitalsClick,
        onSeeAgendaClick = showComingSoon,
        onAccountClick = onProfileClick,
        onLinkWristbandClick = { isLinkWearableOpen = true }
    )
    if (isLinkWearableOpen) {
        LinkWearableSheet(
            careRecipientFirstName = careContext.careRecipientFirstName,
            onLinked = {
                isLinkWearableOpen = false
                Toast.makeText(context, linkedMessage, Toast.LENGTH_SHORT).show()
                viewModel.load()
            },
            onDismiss = { isLinkWearableOpen = false }
        )
    }

}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: LiveVitalsUiState,
    onRetryClick: () -> Unit,
    onBellClick: () -> Unit,
    onChangeRecipientClick: () -> Unit,
    onCallClick: () -> Unit,
    onVideoClick: () -> Unit,
    onLocationClick: () -> Unit,
    onSeeVitalsClick: () -> Unit,
    onSeeAgendaClick: () -> Unit,
    onAccountClick: () -> Unit = {},
    onLinkWristbandClick: () -> Unit = {}
) {
    val careContext = LocalCareContext.current
    val vitals = uiState.vitals
    when {
        // Old readings stay visible during refreshes; the spinner is only for the very first load
        vitals == null && uiState.isLoading -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        vitals == null -> ErrorState(
            modifier = modifier,
            message = uiState.errorMessage ?: stringResource(R.string.health_no_live_readings),
            onRetryClick = onRetryClick
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HomeHeader(
                    userInitials = careContext.userFullName.initials(),
                    userFirstName = careContext.userFirstName,
                    careRecipientFirstName = uiState.careRecipientFirstName,
                    allWithinRange = uiState.allWithinRange,
                    hour = uiState.now.atZone(ZoneId.systemDefault()).hour,
                    onBellClick = onBellClick,
                    onAvatarClick = onAccountClick
                )
            }
            item {
                CareRecipientCard(
                    name = careContext.careRecipientName,
                    hasLiveSignal = uiState.hasLiveSignal,
                    onChangeClick = onChangeRecipientClick
                )
            }
            item {
                StatusHeroCard(
                    careRecipientFirstName = uiState.careRecipientFirstName,
                    allWithinRange = uiState.allWithinRange,
                    hasLiveSignal = uiState.hasLiveSignal,
                    updatedText = relativeTime(vitals.retrievedAt, uiState.now),
                    hasWristband = uiState.hasWristband,
                    onLinkWristbandClick = onLinkWristbandClick
                )
            }
            item {
                QuickActionsRow(onCallClick = onCallClick, onVideoClick = onVideoClick, onLocationClick = onLocationClick)
            }
            item {
                SectionTitle(
                    title = stringResource(R.string.home_vitals_title),
                    action = stringResource(R.string.home_see_detail),
                    onActionClick = onSeeVitalsClick
                )
            }
            item {
                VitalsSummaryCard(
                    vitals = vitals,
                    bloodPressureText = uiState.bloodPressureText,
                    bloodPressureOutOfRange = uiState.bloodPressureOutOfRange,
                    now = uiState.now
                )
            }
            item {
                SectionTitle(
                    title = stringResource(R.string.home_upcoming_title),
                    action = stringResource(R.string.home_see_agenda),
                    onActionClick = onSeeAgendaClick
                )
            }
            item { UpcomingCard() }
        }
    }
}

@Composable
private fun HomeContentPreview(uiState: LiveVitalsUiState) {
    GuardianTheme(dynamicColor = false) {
        HomeContent(
            uiState = uiState,
            onRetryClick = {},
            onBellClick = {},
            onChangeRecipientClick = {},
            onCallClick = {},
            onVideoClick = {},
            onLocationClick = {},
            onSeeVitalsClick = {},
            onSeeAgendaClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun HomeContentOkPreview() {
    HomeContentPreview(
        LiveVitalsUiState(
            careRecipientFirstName = "Elena",
            vitals = previewLiveVitals(),
            hasWristband = true,
            now = previewNow
        )
    )
}

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun HomeContentAttentionPreview() {
    HomeContentPreview(
        LiveVitalsUiState(
            careRecipientFirstName = "Elena",
            vitals = previewLiveVitals(heartRate = 130.0),
            hasWristband = true,
            now = previewNow
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeContentLoadingPreview() {
    HomeContentPreview(LiveVitalsUiState(isLoading = true))
}

@Preview(showBackground = true)
@Composable
private fun HomeContentErrorPreview() {
    HomeContentPreview(LiveVitalsUiState(errorMessage = "No se pudo conectar con el servidor. Revisa tu conexión."))
}
