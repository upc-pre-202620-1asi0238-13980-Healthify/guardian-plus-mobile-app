package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.hydration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.JetBrainsMono
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlan
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineBanner
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineErrorState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineLoadingState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessageEffect
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineProgressBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSectionTitle
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSelectField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSettingsCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTone
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.SettingsRowPadding
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.formatTimeOfDay
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertsettings.component.SettingSwitchRow
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import java.time.Instant
import java.time.LocalTime

// "Meta diaria de agua" and "Intervalo" options of the prototype, inside the platform's 1–20 and 1–12
private val GOAL_OPTIONS = (4..10).toList()
private val INTERVAL_OPTIONS = listOf(1, 2, 3)

/** "Hidratación": today's glasses against the goal and the plan of periodic reminders. */
@Composable
fun HydrationScreen(
    modifier: Modifier = Modifier,
    viewModel: HydrationViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    RoutineMessageEffect(uiState.message, snackbarHostState, viewModel::onMessageShown)

    Box(modifier = modifier) {
        HydrationContent(
            uiState = uiState,
            onBackClick = onBackClick,
            onRetryClick = viewModel::load,
            onActiveChange = viewModel::setActive,
            onGoalSelect = viewModel::setDailyGoal,
            onIntervalSelect = viewModel::setInterval,
            onRespectSleepChange = viewModel::setRespectSleepWindow
        )
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun HydrationContent(
    modifier: Modifier = Modifier,
    uiState: HydrationUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onGoalSelect: (Int) -> Unit,
    onIntervalSelect: (Int) -> Unit,
    onRespectSleepChange: (Boolean) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        RoutineTopBar(
            title = stringResource(R.string.routines_category_hydration),
            subtitle = stringResource(R.string.hydration_subtitle),
            onBackClick = onBackClick
        )
        Box(modifier = Modifier.weight(1f)) {
            when {
                !uiState.isLoaded && uiState.errorMessage == null -> RoutineLoadingState()
                !uiState.isLoaded -> RoutineErrorState(message = uiState.errorMessage.orEmpty(), onRetryClick = onRetryClick)
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item(key = "hero") { HydrationHeroCard(plan = uiState.plan, goal = uiState.settings.dailyGoalGlasses) }
                    item(key = "settings-title") { RoutineSectionTitle(text = stringResource(R.string.hydration_settings_title)) }
                    item(key = "settings") {
                        HydrationSettingsCard(
                            uiState = uiState,
                            onActiveChange = onActiveChange,
                            onGoalSelect = onGoalSelect,
                            onIntervalSelect = onIntervalSelect,
                            onRespectSleepChange = onRespectSleepChange
                        )
                    }
                    if (uiState.settings.respectSleepWindow) {
                        item(key = "rest") {
                            RoutineBanner(
                                iconRes = R.drawable.ic_moon,
                                title = stringResource(R.string.hydration_rest_title),
                                message = stringResource(R.string.hydration_rest_message),
                                tone = RoutineTone.MINT
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Water ring "4 de 6 vasos", what is left for the goal and when the next reminder is due. */
@Composable
private fun HydrationHeroCard(plan: HydrationPlan?, goal: Int) {
    val consumed = plan?.glassesConsumedToday ?: 0
    val remaining = plan?.remainingGlassesToday ?: goal
    val nextReminderAt = plan?.nextReminderAt

    RoutineCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            WaterRing(consumed = consumed, goal = goal)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.hydration_goal_label), style = MaterialTheme.typography.dataLabel, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = if (remaining > 0) {
                        pluralStringResource(R.plurals.hydration_remaining, remaining, remaining)
                    } else {
                        stringResource(R.string.hydration_goal_reached)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when {
                        plan == null -> stringResource(R.string.hydration_no_plan)
                        !plan.active -> stringResource(R.string.hydration_paused)
                        nextReminderAt != null -> stringResource(R.string.hydration_next_reminder, formatClockTime(nextReminderAt))
                        else -> stringResource(R.string.hydration_no_pending)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                RoutineProgressBar(modifier = Modifier.padding(top = 12.dp), percentage = plan?.progressPercentage ?: 0)
            }
        }
    }
}

@Composable
private fun WaterRing(consumed: Int, goal: Int) {
    Column(
        modifier = Modifier
            .size(112.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .border(BorderStroke(6.dp, MaterialTheme.colorScheme.secondary), CircleShape),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_droplet),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = consumed.toString(),
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold, fontSize = 23.sp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = pluralStringResource(R.plurals.hydration_of_goal, goal, goal),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal, fontSize = 9.sp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun HydrationSettingsCard(
    uiState: HydrationUiState,
    onActiveChange: (Boolean) -> Unit,
    onGoalSelect: (Int) -> Unit,
    onIntervalSelect: (Int) -> Unit,
    onRespectSleepChange: (Boolean) -> Unit
) {
    val settings = uiState.settings
    val enabled = !uiState.isSaving
    val sleepStart = uiState.plan?.sleepWindowStart
    val sleepEnd = uiState.plan?.sleepWindowEnd

    RoutineSettingsCard {
        SettingSwitchRow(
            title = stringResource(R.string.hydration_active),
            detail = stringResource(R.string.hydration_active_detail, settings.intervalHours * 60),
            checked = settings.active,
            enabled = enabled,
            onCheckedChange = onActiveChange
        )
        RoutineSelectField(
            modifier = SettingsRowPadding,
            label = stringResource(R.string.hydration_goal),
            // A goal set elsewhere (e.g. 12 through the API) still shows as selected
            options = (GOAL_OPTIONS + settings.dailyGoalGlasses).distinct().sorted(),
            selected = settings.dailyGoalGlasses,
            optionLabel = { pluralStringResource(R.plurals.hydration_glasses, it, it) },
            enabled = enabled,
            onSelect = onGoalSelect
        )
        RoutineSelectField(
            modifier = SettingsRowPadding,
            label = stringResource(R.string.hydration_interval),
            options = (INTERVAL_OPTIONS + settings.intervalHours).distinct().sorted(),
            selected = settings.intervalHours,
            optionLabel = {
                if (it == 1) stringResource(R.string.routines_hydration_every_hour) else stringResource(R.string.routines_hydration_every_hours, it)
            },
            enabled = enabled,
            onSelect = onIntervalSelect
        )
        SettingSwitchRow(
            title = stringResource(R.string.hydration_respect_sleep),
            detail = if (sleepStart != null && sleepEnd != null) {
                stringResource(R.string.hydration_respect_sleep_detail, formatTimeOfDay(sleepStart), formatTimeOfDay(sleepEnd))
            } else {
                stringResource(R.string.hydration_respect_sleep_detail_generic)
            },
            checked = settings.respectSleepWindow,
            enabled = enabled,
            onCheckedChange = onRespectSleepChange
        )
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun HydrationContentPreview() {
    GuardianTheme(dynamicColor = false) {
        HydrationContent(
            uiState = HydrationUiState(
                isLoaded = true,
                plan = HydrationPlan(
                    active = true,
                    dailyGoalGlasses = 6,
                    intervalHours = 2,
                    respectSleepWindow = true,
                    sleepWindowStart = LocalTime.of(22, 0),
                    sleepWindowEnd = LocalTime.of(7, 0),
                    glassesConsumedToday = 4,
                    remainingGlassesToday = 2,
                    nextReminderAt = Instant.parse("2026-10-08T21:00:00Z")
                ),
                settings = HydrationSettings(active = true, dailyGoalGlasses = 6, intervalHours = 2, respectSleepWindow = true)
            ),
            onBackClick = {},
            onRetryClick = {},
            onActiveChange = {},
            onGoalSelect = {},
            onIntervalSelect = {},
            onRespectSleepChange = {}
        )
    }
}
