package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.session.LocalCareContext
import com.example.guardian_plus_mobile_app.core.text.initials
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitor
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlan
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepClassification
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecord
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineErrorState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineLoadingState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.formatWeekdayAndDay
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.component.NextRoutineCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.component.RoutineCategoryCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.component.RoutinesHeader
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.component.TodayProgressCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.component.WristbandSyncNote
import java.time.Duration
import java.time.Instant
import java.time.LocalTime

/** Routines tab of the bottom bar: today's progress, what comes next and every routine of Care Routines. */
@Composable
fun RoutinesScreen(
    modifier: Modifier = Modifier,
    viewModel: RoutinesViewModel = hiltViewModel(),
    onCategoryClick: (RoutineCategory) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Coming back from a routine screen, the day may have changed there
    LaunchedEffect(Unit) {
        viewModel.onScreenShown()
    }

    RoutinesContent(
        modifier = modifier,
        uiState = uiState,
        onRetryClick = viewModel::load,
        onNextClick = { uiState.nextReminder?.let { onCategoryClick(RoutineCategory.of(it.type)) } },
        onCategoryClick = onCategoryClick
    )
}

@Composable
fun RoutinesContent(
    modifier: Modifier = Modifier,
    uiState: RoutinesUiState,
    onRetryClick: () -> Unit,
    onNextClick: () -> Unit,
    onCategoryClick: (RoutineCategory) -> Unit
) {
    val careContext = LocalCareContext.current
    Column(modifier = modifier.fillMaxSize()) {
        RoutinesHeader(
            userInitials = careContext.userFullName.initials(),
            careRecipientName = uiState.careRecipientName
        )
        Box(modifier = Modifier.weight(1f)) {
            when {
                // Old data stays visible during refreshes; the spinner is only for the very first load
                !uiState.isLoaded && uiState.errorMessage == null -> RoutineLoadingState()

                !uiState.isLoaded -> RoutineErrorState(
                    message = uiState.errorMessage.orEmpty(),
                    onRetryClick = onRetryClick
                )

                else -> RoutinesList(uiState = uiState, onNextClick = onNextClick, onCategoryClick = onCategoryClick)
            }
        }
    }
}

@Composable
private fun RoutinesList(
    uiState: RoutinesUiState,
    onNextClick: () -> Unit,
    onCategoryClick: (RoutineCategory) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "today") {
            TodayProgressCard(dateLabel = formatWeekdayAndDay(uiState.now), count = uiState.today)
        }
        uiState.nextReminder?.let { next ->
            item(key = "next-title") { SectionTitle(text = stringResource(R.string.routines_next_title)) }
            item(key = "next-${next.id}") {
                NextRoutineCard(reminder = next, now = uiState.now, onClick = onNextClick)
            }
        }
        item(key = "all-title") { SectionTitle(text = stringResource(R.string.routines_all_title)) }
        items(RoutineCategory.entries, key = { it.name }) { category ->
            RoutineCategoryCard(
                category = category,
                summary = uiState.summaryOf(category),
                onClick = { onCategoryClick(category) }
            )
        }
        uiState.activityMonitor?.lastSampleAt?.let { lastSyncAt ->
            item(key = "sync") {
                WristbandSyncNote(
                    modifier = Modifier.padding(top = 14.dp),
                    lastSyncAt = lastSyncAt,
                    now = uiState.now
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 14.dp)
    )
}

private val previewNow: Instant = Instant.parse("2026-10-08T18:35:00Z")

private fun previewReminder(
    id: String,
    type: ReminderType,
    title: String,
    minutesFromNow: Long,
    status: ReminderStatus,
    dosage: String? = null,
    instructions: String? = null,
    durationMinutes: Int? = null
) = Reminder(
    id = id,
    personUnderCareId = "elena",
    type = type,
    title = title,
    dosage = dosage,
    instructions = instructions,
    durationMinutes = durationMinutes,
    scheduledTime = previewNow + Duration.ofMinutes(minutesFromNow),
    status = status
)

private val previewState = RoutinesUiState(
    isLoaded = true,
    careRecipientName = "Elena Rojas",
    todayReminders = listOf(
        previewReminder("1", ReminderType.MEDICATION, "Omeprazol 20 mg", -400, ReminderStatus.CONFIRMED),
        previewReminder("2", ReminderType.MEDICATION, "Metformina 850 mg", -360, ReminderStatus.CONFIRMED),
        previewReminder("3", ReminderType.PHYSICAL_ACTIVITY, "Movilidad de hombros", -300, ReminderStatus.CONFIRMED),
        previewReminder("4", ReminderType.APPOINTMENT, "Análisis de sangre", -240, ReminderStatus.CONFIRMED),
        previewReminder("5", ReminderType.PHYSICAL_ACTIVITY, "Caminata suave", -120, ReminderStatus.CONFIRMED),
        previewReminder(
            "6", ReminderType.MEDICATION, "Losartán 50 mg", 25, ReminderStatus.SCHEDULED,
            dosage = "1 tableta", instructions = "Después del almuerzo"
        ),
        previewReminder("7", ReminderType.PHYSICAL_ACTIVITY, "Estiramiento de piernas", 180, ReminderStatus.SCHEDULED)
    ),
    nextAppointment = previewReminder("8", ReminderType.APPOINTMENT, "Control de cardiología", 960, ReminderStatus.SCHEDULED),
    hydrationPlan = HydrationPlan(
        active = true,
        dailyGoalGlasses = 6,
        intervalHours = 2,
        respectSleepWindow = true,
        sleepWindowStart = LocalTime.of(22, 0),
        sleepWindowEnd = LocalTime.of(7, 0),
        glassesConsumedToday = 4,
        remainingGlassesToday = 2,
        nextReminderAt = null
    ),
    lastSleep = SleepCycleRecord(
        id = "s1",
        startTime = previewNow - Duration.ofHours(19),
        endTime = previewNow - Duration.ofHours(12),
        durationMinutes = 444,
        interruptionCount = 5,
        continuityScore = 76,
        classification = SleepClassification.FRAGMENTED
    ),
    activityMonitor = ActivityMonitor(
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
    medicationStocks = listOf(
        MedicationStock(
            id = "st1",
            medicationName = "Losartán",
            dosage = "50 mg",
            remainingDoses = 6,
            dailyConsumption = 2.0,
            packageSize = 30,
            remainingDaysOfSupply = 3.0,
            estimatedDepletionDate = null,
            restockRecommended = true
        )
    ),
    now = previewNow
)

@Preview(showBackground = true, heightDp = 1300)
@Composable
private fun RoutinesContentPreview() {
    GuardianTheme(dynamicColor = false) {
        RoutinesContent(uiState = previewState, onRetryClick = {}, onNextClick = {}, onCategoryClick = {})
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun RoutinesContentEmptyPreview() {
    GuardianTheme(dynamicColor = false) {
        RoutinesContent(
            uiState = RoutinesUiState(isLoaded = true, careRecipientName = "Elena Rojas", now = previewNow),
            onRetryClick = {},
            onNextClick = {},
            onCategoryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoutinesContentLoadingPreview() {
    GuardianTheme(dynamicColor = false) {
        RoutinesContent(
            uiState = RoutinesUiState(isLoading = true, careRecipientName = "Elena Rojas"),
            onRetryClick = {},
            onNextClick = {},
            onCategoryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoutinesContentErrorPreview() {
    GuardianTheme(dynamicColor = false) {
        RoutinesContent(
            uiState = RoutinesUiState(
                careRecipientName = "Elena Rojas",
                errorMessage = "No se pudo conectar con el servidor. Revisa tu conexión."
            ),
            onRetryClick = {},
            onNextClick = {},
            onCategoryClick = {}
        )
    }
}
