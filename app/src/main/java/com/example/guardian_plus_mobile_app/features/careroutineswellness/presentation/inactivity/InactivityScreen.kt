package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.inactivity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityLogEntry
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityLogEntryType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitor
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineBanner
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineErrorState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineLoadingState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessageEffect
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSectionTitle
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSelectField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSettingsCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTone
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.SettingsRowPadding
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.formatTimeOfDay
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertsettings.component.SettingSwitchRow
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.StatusChip
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.relativeTime
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import kotlin.math.roundToInt

// "Alertar después de" options of the prototype, inside the platform's 30–240 minutes
private val THRESHOLD_OPTIONS = listOf(60, 90, 120)

/** "Inactividad": whether the person is moving, how inactivity is watched and the recent activity. */
@Composable
fun InactivityScreen(
    modifier: Modifier = Modifier,
    viewModel: InactivityViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    RoutineMessageEffect(uiState.message, snackbarHostState, viewModel::onMessageShown)

    Box(modifier = modifier) {
        InactivityContent(
            uiState = uiState,
            onBackClick = onBackClick,
            onRetryClick = viewModel::load,
            onDetectionChange = viewModel::setDetectionEnabled,
            onThresholdSelect = viewModel::setThreshold
        )
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun InactivityContent(
    modifier: Modifier = Modifier,
    uiState: InactivityUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onDetectionChange: (Boolean) -> Unit,
    onThresholdSelect: (Int) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        RoutineTopBar(
            title = stringResource(R.string.routines_category_inactivity),
            subtitle = stringResource(R.string.inactivity_subtitle),
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
                    item(key = "status") { ActivityStatusCard(monitor = uiState.monitor, now = uiState.now) }
                    item(key = "watch-title") { RoutineSectionTitle(text = stringResource(R.string.inactivity_watch_title)) }
                    item(key = "watch") {
                        WatchSettingsCard(
                            uiState = uiState,
                            onDetectionChange = onDetectionChange,
                            onThresholdSelect = onThresholdSelect
                        )
                    }
                    item(key = "what-happens") {
                        RoutineBanner(
                            iconRes = R.drawable.ic_triangle_alert,
                            title = stringResource(R.string.inactivity_threshold_title, uiState.thresholdMinutes),
                            message = stringResource(R.string.inactivity_threshold_message, DemoSession.CARE_RECIPIENT_FIRST_NAME),
                            tone = RoutineTone.ORANGE
                        )
                    }
                    item(key = "reset") {
                        RoutineBanner(
                            iconRes = R.drawable.ic_activity,
                            title = stringResource(R.string.inactivity_reset_title),
                            message = stringResource(R.string.inactivity_reset_message),
                            tone = RoutineTone.MINT
                        )
                    }
                    item(key = "recent-title") { RoutineSectionTitle(text = stringResource(R.string.inactivity_recent_title)) }
                    item(key = "recent") { RecentActivityCard(entries = uiState.recentActivity) }
                }
            }
        }
    }
}

/** Green while the person moves, orange once prolonged inactivity is detected. */
@Composable
private fun ActivityStatusCard(monitor: ActivityMonitor?, now: Instant) {
    val scheme = MaterialTheme.colorScheme
    val inactive = monitor?.status == ActivityStatus.INACTIVITY_DETECTED
    val container = when {
        monitor == null -> scheme.surfaceVariant
        inactive -> scheme.tertiary
        else -> scheme.primary
    }
    val content = if (monitor == null) scheme.onSurfaceVariant else scheme.onPrimary
    val softContent = if (monitor == null) scheme.onSurfaceVariant else scheme.primaryContainer
    val lastMovementAt = monitor?.lastMovementAt
    val inactivitySince = monitor?.inactivitySince

    Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = container) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(content.copy(alpha = 0.13f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(if (inactive) R.drawable.ic_hourglass else R.drawable.ic_activity),
                    contentDescription = null,
                    tint = content,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (monitor != null) {
                    StatusChip(
                        text = stringResource(if (inactive) R.string.inactivity_badge_still else R.string.inactivity_badge_moving),
                        container = content.copy(alpha = 0.18f),
                        content = content
                    )
                }
                Text(
                    text = stringResource(
                        when {
                            monitor == null -> R.string.inactivity_status_no_data
                            inactive -> R.string.inactivity_status_detected
                            else -> R.string.inactivity_status_normal
                        }
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = content,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    text = when {
                        monitor == null -> stringResource(R.string.inactivity_status_no_data_detail)
                        inactive && inactivitySince != null -> stringResource(R.string.inactivity_still_since, formatClockTime(inactivitySince))
                        lastMovementAt != null -> stringResource(
                            R.string.inactivity_last_movement,
                            relativeTime(lastMovementAt, now).replaceFirstChar { it.lowercase() }
                        )
                        else -> stringResource(R.string.inactivity_no_movement_yet)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = softContent
                )
            }
        }
    }
}

@Composable
private fun WatchSettingsCard(
    uiState: InactivityUiState,
    onDetectionChange: (Boolean) -> Unit,
    onThresholdSelect: (Int) -> Unit
) {
    val monitor = uiState.monitor
    val watchStart = monitor?.watchHoursStart
    val watchEnd = monitor?.watchHoursEnd
    val enabled = !uiState.isSaving

    RoutineSettingsCard {
        SettingSwitchRow(
            title = stringResource(R.string.inactivity_detection),
            detail = if (watchStart != null && watchEnd != null) {
                stringResource(R.string.inactivity_detection_detail, formatTimeOfDay(watchStart), formatTimeOfDay(watchEnd))
            } else {
                stringResource(R.string.inactivity_detection_detail_generic)
            },
            checked = uiState.detectionEnabled,
            enabled = enabled,
            onCheckedChange = onDetectionChange
        )
        RoutineSelectField(
            modifier = SettingsRowPadding,
            label = stringResource(R.string.inactivity_threshold),
            // A threshold set elsewhere (e.g. 45 through the API) still shows as selected
            options = (THRESHOLD_OPTIONS + uiState.thresholdMinutes).distinct().sorted(),
            selected = uiState.thresholdMinutes,
            optionLabel = { stringResource(R.string.routines_activity_duration, it) },
            enabled = enabled && uiState.detectionEnabled,
            onSelect = onThresholdSelect
        )
        monitor?.let { InactivityCounter(modifier = SettingsRowPadding, inactiveMinutes = it.inactiveMinutes) }
    }
}

/** "CONTADOR ACTUAL 03:18 minutos sin movimiento". */
@Composable
private fun InactivityCounter(modifier: Modifier = Modifier, inactiveMinutes: Double) {
    val totalSeconds = (inactiveMinutes * 60).roundToInt()
    Surface(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.inactivity_counter_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60),
                style = MaterialTheme.typography.dataMetric,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.inactivity_counter_unit),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RecentActivityCard(entries: List<ActivityLogEntry>) {
    RoutineCard {
        if (entries.isEmpty()) {
            Text(
                text = stringResource(R.string.inactivity_recent_none),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        entries.forEachIndexed { index, entry ->
            if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            EventRow(entry = entry)
        }
    }
}

/** "Movimiento detectado · 13:42 · Contador reiniciado a cero". */
@Composable
private fun EventRow(entry: ActivityLogEntry) {
    val scheme = MaterialTheme.colorScheme
    val time = formatClockTime(entry.occurredAt)
    val (title, detail, dot) = when (entry.type) {
        ActivityLogEntryType.MOVEMENT_DETECTED -> Triple(
            stringResource(R.string.inactivity_event_movement),
            stringResource(R.string.inactivity_event_movement_detail, time),
            scheme.primary
        )
        ActivityLogEntryType.WALK_DETECTED -> Triple(
            stringResource(R.string.inactivity_event_walk),
            entry.durationMinutes?.let { pluralStringResource(R.plurals.inactivity_event_walk_detail, it, time, it) } ?: time,
            scheme.primary
        )
        ActivityLogEntryType.PROLONGED_INACTIVITY_DETECTED -> Triple(
            stringResource(R.string.inactivity_event_inactivity),
            entry.inactiveMinutes?.let { stringResource(R.string.inactivity_event_inactivity_detail, time, it.roundToInt()) } ?: time,
            scheme.tertiary
        )
    }
    Row(modifier = Modifier.padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
        EventDot(color = dot)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = scheme.onSurface)
            Text(text = detail, style = MaterialTheme.typography.bodySmall, color = scheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EventDot(color: Color) {
    Box(
        modifier = Modifier
            .size(14.dp)
            .border(BorderStroke(4.dp, color.copy(alpha = 0.2f)), CircleShape)
            .padding(4.dp)
            .background(color, CircleShape)
    )
}

private val previewNow: Instant = Instant.parse("2026-10-08T18:35:00Z")

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun InactivityContentPreview() {
    GuardianTheme(dynamicColor = false) {
        InactivityContent(
            uiState = InactivityUiState(
                isLoaded = true,
                monitor = ActivityMonitor(
                    status = ActivityStatus.NORMAL,
                    inactivitySince = null,
                    inactiveMinutes = 3.3,
                    lastMovementAt = previewNow - Duration.ofMinutes(3),
                    lastSampleAt = previewNow - Duration.ofMinutes(1),
                    detectionEnabled = true,
                    thresholdMinutes = 60,
                    watchHoursStart = LocalTime.of(7, 0),
                    watchHoursEnd = LocalTime.of(22, 0)
                ),
                detectionEnabled = true,
                thresholdMinutes = 60,
                recentActivity = listOf(
                    ActivityLogEntry("1", ActivityLogEntryType.MOVEMENT_DETECTED, previewNow - Duration.ofMinutes(50), null, null, 42.0),
                    ActivityLogEntry("2", ActivityLogEntryType.WALK_DETECTED, previewNow - Duration.ofHours(3), 12, 1100, null)
                ),
                now = previewNow
            ),
            onBackClick = {},
            onRetryClick = {},
            onDetectionChange = {},
            onThresholdSelect = {}
        )
    }
}
