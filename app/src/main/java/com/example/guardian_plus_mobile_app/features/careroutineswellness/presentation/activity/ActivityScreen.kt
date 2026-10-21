package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Adherence
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.DailyAdherence
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineEmptyState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineErrorState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineFormSection
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineLoadingState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessageEffect
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutinePrimaryButton
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineProgressBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSectionTitle
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSelectField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTextField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTimeField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTimelineItem
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTone
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBarButton
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.TimelineState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.displayTitle
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.weekdayInitial
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

// "Duración" options of the prototype, in minutes
private val DURATIONS = listOf(5, 10, 15)

/** "Actividad ligera": today's mobility blocks, how many were done and the last week. */
@Composable
fun ActivityScreen(
    modifier: Modifier = Modifier,
    viewModel: ActivityViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    RoutineMessageEffect(uiState.message, snackbarHostState, viewModel::onMessageShown)

    Box(modifier = modifier) {
        ActivityContent(
            uiState = uiState,
            onBackClick = onBackClick,
            onAddClick = viewModel::toggleAdding,
            onRetryClick = viewModel::load,
            onCompleteBlock = viewModel::completeBlock,
            isLaterToday = viewModel::isLaterToday,
            onAddActivity = viewModel::addActivity
        )
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun ActivityContent(
    modifier: Modifier = Modifier,
    uiState: ActivityUiState,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onRetryClick: () -> Unit,
    onCompleteBlock: (Reminder) -> Unit,
    isLaterToday: (LocalTime) -> Boolean,
    onAddActivity: (name: String, time: LocalTime, durationMinutes: Int) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        RoutineTopBar(
            title = stringResource(R.string.routines_category_activity),
            subtitle = stringResource(R.string.activity_subtitle),
            onBackClick = onBackClick,
            action = {
                RoutineTopBarButton(
                    iconRes = if (uiState.isAdding) R.drawable.ic_x else R.drawable.ic_plus,
                    contentDescription = stringResource(if (uiState.isAdding) R.string.action_cancel else R.string.activity_form_title),
                    onClick = onAddClick
                )
            }
        )
        Box(modifier = Modifier.weight(1f)) {
            when {
                !uiState.isLoaded && uiState.errorMessage == null -> RoutineLoadingState()
                !uiState.isLoaded -> RoutineErrorState(message = uiState.errorMessage.orEmpty(), onRetryClick = onRetryClick)
                uiState.isAdding -> AddActivityForm(isSaving = uiState.isSaving, isLaterToday = isLaterToday, onAdd = onAddActivity)
                else -> ActivityList(uiState = uiState, onAddClick = onAddClick, onCompleteBlock = onCompleteBlock)
            }
        }
    }
}

@Composable
private fun ActivityList(uiState: ActivityUiState, onAddClick: () -> Unit, onCompleteBlock: (Reminder) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "hero") { ActivityHeroCard(completed = uiState.completedCount, total = uiState.blocks.size, percentage = uiState.percentage) }
        item(key = "plan-title") { RoutineSectionTitle(text = stringResource(R.string.activity_plan_title)) }
        if (uiState.blocks.isEmpty()) {
            item(key = "empty") {
                RoutineEmptyState(
                    iconRes = R.drawable.ic_activity,
                    title = stringResource(R.string.activity_empty_title),
                    message = stringResource(R.string.activity_empty_message),
                    actionLabel = stringResource(R.string.activity_form_title),
                    onActionClick = onAddClick
                )
            }
        }
        items(uiState.blocks, key = { it.id }) { block ->
            ActivityBlockItem(
                block = block,
                isNext = block.id == uiState.nextBlock?.id,
                isBusy = uiState.busyReminderId == block.id,
                onCompleteClick = { onCompleteBlock(block) }
            )
        }
        item(key = "week-title") { RoutineSectionTitle(text = stringResource(R.string.activity_week_title)) }
        item(key = "week") { WeeklyActivityCard(adherence = uiState.weeklyAdherence) }
    }
}

/** Green card "PROGRESO DE HOY · 2 de 3 completadas" with the daily adherence bar. */
@Composable
private fun ActivityHeroCard(completed: Int, total: Int, percentage: Int) {
    val scheme = MaterialTheme.colorScheme
    Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = scheme.primary) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(scheme.onPrimary.copy(alpha = 0.13f), MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(R.drawable.ic_activity), contentDescription = null, tint = scheme.onPrimary, modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = stringResource(R.string.activity_hero_label), style = MaterialTheme.typography.dataLabel, color = scheme.onPrimary)
                    Text(
                        text = stringResource(R.string.routines_activity_progress, completed, total),
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.onPrimary
                    )
                    Text(text = stringResource(R.string.activity_hero_message), style = MaterialTheme.typography.bodySmall, color = scheme.primaryContainer)
                }
            }
            RoutineProgressBar(
                modifier = Modifier.padding(top = 14.dp),
                percentage = percentage,
                label = stringResource(R.string.activity_hero_progress),
                track = scheme.onPrimary.copy(alpha = 0.2f),
                progress = scheme.primaryContainer,
                textColor = scheme.primaryContainer
            )
        }
    }
}

@Composable
private fun ActivityBlockItem(block: Reminder, isNext: Boolean, isBusy: Boolean, onCompleteClick: () -> Unit) {
    val duration = block.durationMinutes?.let { stringResource(R.string.routines_activity_duration, it) }
    val confirmedAt = block.confirmedAt
    val (state, badgeRes, tone) = when (block.status) {
        ReminderStatus.CONFIRMED -> Triple(TimelineState.DONE, R.string.activity_status_done, RoutineTone.MINT)
        ReminderStatus.MISSED -> Triple(TimelineState.MISSED, R.string.activity_status_missed, RoutineTone.ORANGE)
        ReminderStatus.ISSUED, ReminderStatus.REISSUED -> Triple(TimelineState.CURRENT, R.string.activity_status_pending, RoutineTone.YELLOW)
        else -> if (isNext) {
            Triple(TimelineState.CURRENT, R.string.activity_status_next, RoutineTone.YELLOW)
        } else {
            Triple(TimelineState.UPCOMING, R.string.activity_status_scheduled, RoutineTone.MUTED)
        }
    }
    val second = when {
        confirmedAt != null -> stringResource(R.string.activity_confirmed_at, formatClockTime(confirmedAt))
        block.canBeConfirmed -> stringResource(R.string.activity_notified)
        else -> null
    }
    RoutineTimelineItem(
        time = formatClockTime(block.scheduledTime),
        iconRes = R.drawable.ic_activity,
        title = block.displayTitle(),
        detail = listOfNotNull(duration, second).joinToString(" · "),
        badge = stringResource(badgeRes),
        badgeTone = tone,
        state = state,
        actionLabel = if (block.canBeConfirmed) stringResource(R.string.activity_complete_action) else null,
        isActionLoading = isBusy,
        onActionClick = onCompleteClick
    )
}

/** Bars of the last seven days, one per day, with the share of blocks that were done. */
@Composable
private fun WeeklyActivityCard(adherence: Adherence?) {
    RoutineCard {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.activity_week_chart_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(text = stringResource(R.string.routines_last_seven_days), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (adherence == null) {
            Text(
                text = stringResource(R.string.routines_adherence_unavailable),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
            return@RoutineCard
        }
        WeekBars(days = adherence.days, modifier = Modifier.padding(top = 14.dp))
        Text(
            text = if (adherence.hasDueReminders) {
                stringResource(R.string.activity_week_caption, adherence.confirmed, adherence.percentage)
            } else {
                stringResource(R.string.activity_week_none)
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}

@Composable
private fun WeekBars(modifier: Modifier = Modifier, days: List<DailyAdherence>) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(118.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEach { day ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // A day without blocks keeps a stub so the week still reads as seven days
                val fraction = if (day.due == 0) 0f else day.percentage / 100f
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((90 * fraction).dp.coerceAtLeast(6.dp))
                        .background(
                            if (day.due == 0) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 2.dp, bottomEnd = 2.dp)
                        )
                )
                Text(
                    text = weekdayInitial(day.date.dayOfWeek),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }
}

/** "Nueva actividad": a block for later today. */
@Composable
private fun AddActivityForm(
    isSaving: Boolean,
    isLaterToday: (LocalTime) -> Boolean,
    onAdd: (name: String, time: LocalTime, durationMinutes: Int) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var time by rememberSaveable { mutableStateOf(LocalTime.now().plusHours(1).withMinute(0)) }
    var duration by rememberSaveable { mutableStateOf(DURATIONS[1]) }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    val nameValid = name.isNotBlank()
    val timeValid = isLaterToday(time)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp)
    ) {
        RoutineFormSection(title = stringResource(R.string.activity_form_title)) {
            RoutineTextField(
                label = stringResource(R.string.activity_form_name),
                value = name,
                onValueChange = { name = it },
                placeholder = stringResource(R.string.activity_form_name_hint),
                errorMessage = if (showErrors && !nameValid) stringResource(R.string.activity_form_name_error) else null
            )
            RoutineTimeField(
                label = stringResource(R.string.activity_form_time),
                value = time,
                errorMessage = if (showErrors && !timeValid) stringResource(R.string.activity_form_time_error) else null,
                onValueChange = { time = it }
            )
            RoutineSelectField(
                label = stringResource(R.string.activity_form_duration),
                options = DURATIONS,
                selected = duration,
                optionLabel = { stringResource(R.string.routines_activity_duration, it) },
                onSelect = { duration = it }
            )
            RoutinePrimaryButton(
                text = stringResource(R.string.activity_form_save),
                isLoading = isSaving,
                onClick = {
                    showErrors = true
                    if (nameValid && timeValid) onAdd(name, time, duration)
                }
            )
        }
    }
}

private val previewNow: Instant = Instant.parse("2026-10-08T18:35:00Z")

private fun previewBlock(id: String, title: String, minutesFromNow: Long, status: ReminderStatus, minutes: Int) = Reminder(
    id = id,
    personUnderCareId = "elena",
    type = ReminderType.PHYSICAL_ACTIVITY,
    title = title,
    durationMinutes = minutes,
    scheduledTime = previewNow + Duration.ofMinutes(minutesFromNow),
    confirmedAt = if (status == ReminderStatus.CONFIRMED) previewNow + Duration.ofMinutes(minutesFromNow + 7) else null,
    status = status
)

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun ActivityContentPreview() {
    val today = LocalDate.parse("2026-10-08")
    GuardianTheme(dynamicColor = false) {
        ActivityContent(
            uiState = ActivityUiState(
                isLoaded = true,
                todayReminders = listOf(
                    previewBlock("1", "Movilidad de hombros", -300, ReminderStatus.CONFIRMED, 5),
                    previewBlock("2", "Caminata suave", -120, ReminderStatus.CONFIRMED, 10),
                    previewBlock("3", "Estiramiento de piernas", -2, ReminderStatus.ISSUED, 5)
                ),
                weeklyAdherence = Adherence(
                    type = ReminderType.PHYSICAL_ACTIVITY,
                    from = today.minusDays(6),
                    to = today,
                    due = 17,
                    confirmed = 14,
                    percentage = 82,
                    days = listOf(80, 100, 66, 100, 66, 35, 10).mapIndexed { index, value ->
                        DailyAdherence(today.minusDays(6L - index), 3, 2, value)
                    }
                ),
                now = previewNow
            ),
            onBackClick = {},
            onAddClick = {},
            onRetryClick = {},
            onCompleteBlock = {},
            isLaterToday = { true },
            onAddActivity = { _, _, _ -> }
        )
    }
}
