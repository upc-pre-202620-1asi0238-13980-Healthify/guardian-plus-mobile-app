package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.medication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Adherence
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineBadge
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineBanner
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineEmptyState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineErrorState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineLoadingState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessageEffect
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineProgressBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSectionTitle
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTimelineItem
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTone
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBarButton
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.TimelineState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.detailLine
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.displayTitle
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.durationUntil
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

/** "Medicación": today's doses, the ones waiting for review and the adherence of the week. */
@Composable
fun MedicationScreen(
    modifier: Modifier = Modifier,
    viewModel: MedicationViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddDoseClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    RoutineMessageEffect(uiState.message, snackbarHostState, viewModel::onMessageShown)

    Box(modifier = modifier) {
        MedicationContent(
            uiState = uiState,
            onBackClick = onBackClick,
            onAddDoseClick = onAddDoseClick,
            onRetryClick = viewModel::load,
            onConfirmDose = viewModel::confirmDose,
            onMarkDoseAsMissed = viewModel::markDoseAsMissed
        )
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun MedicationContent(
    modifier: Modifier = Modifier,
    uiState: MedicationUiState,
    onBackClick: () -> Unit,
    onAddDoseClick: () -> Unit,
    onRetryClick: () -> Unit,
    onConfirmDose: (Reminder) -> Unit,
    onMarkDoseAsMissed: (Reminder) -> Unit
) {
    // Marking a dose as missed closes it for good, so it is asked first
    var reviewingId by rememberSaveable { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        RoutineTopBar(
            title = stringResource(R.string.routines_category_medication),
            subtitle = stringResource(R.string.medication_subtitle),
            onBackClick = onBackClick,
            action = {
                RoutineTopBarButton(
                    iconRes = R.drawable.ic_plus,
                    contentDescription = stringResource(R.string.medication_form_title),
                    onClick = onAddDoseClick
                )
            }
        )
        Box(modifier = Modifier.weight(1f)) {
            when {
                !uiState.isLoaded && uiState.errorMessage == null -> RoutineLoadingState()
                !uiState.isLoaded -> RoutineErrorState(message = uiState.errorMessage.orEmpty(), onRetryClick = onRetryClick)
                else -> MedicationList(
                    uiState = uiState,
                    onAddDoseClick = onAddDoseClick,
                    onConfirmDose = onConfirmDose,
                    onReviewDose = { reviewingId = it.id }
                )
            }
        }
    }

    uiState.unconfirmedDoses.firstOrNull { it.id == reviewingId }?.let { dose ->
        AlertDialog(
            onDismissRequest = { reviewingId = null },
            title = { Text(text = stringResource(R.string.medication_review_title, dose.displayTitle())) },
            text = { Text(text = stringResource(R.string.medication_review_message, DemoSession.CARE_RECIPIENT_FIRST_NAME)) },
            confirmButton = {
                TextButton(onClick = {
                    reviewingId = null
                    onMarkDoseAsMissed(dose)
                }) {
                    Text(text = stringResource(R.string.medication_review_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { reviewingId = null }) { Text(text = stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Composable
private fun MedicationList(
    uiState: MedicationUiState,
    onAddDoseClick: () -> Unit,
    onConfirmDose: (Reminder) -> Unit,
    onReviewDose: (Reminder) -> Unit
) {
    val doses = uiState.doses
    val nextDose = uiState.nextDose

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        nextDose?.let { dose ->
            item(key = "next") { NextDoseBanner(dose = dose, now = uiState.now) }
        }
        item(key = "today-title") {
            RoutineSectionTitle(
                text = stringResource(R.string.medication_today_title),
                trailing = if (doses.isEmpty()) {
                    null
                } else {
                    { RoutineBadge(text = stringResource(R.string.medication_taken_count, uiState.takenCount, doses.size)) }
                }
            )
        }
        if (doses.isEmpty()) {
            item(key = "empty") {
                RoutineEmptyState(
                    iconRes = R.drawable.ic_pill,
                    title = stringResource(R.string.medication_empty_title),
                    message = stringResource(R.string.medication_empty_message),
                    actionLabel = stringResource(R.string.medication_form_title),
                    onActionClick = onAddDoseClick
                )
            }
        }
        items(doses, key = { it.id }) { dose ->
            DoseItem(
                dose = dose,
                isNext = dose.id == nextDose?.id,
                isBusy = uiState.busyReminderId == dose.id,
                onConfirmClick = { onConfirmDose(dose) }
            )
        }
        items(uiState.unconfirmedDoses, key = { "review-${it.id}" }) { dose ->
            RoutineBanner(
                iconRes = R.drawable.ic_triangle_alert,
                title = stringResource(R.string.medication_unconfirmed_title, dose.displayTitle()),
                message = stringResource(R.string.medication_unconfirmed_message),
                tone = RoutineTone.ORANGE,
                actionLabel = stringResource(R.string.medication_unconfirmed_action),
                isActionLoading = uiState.busyReminderId == dose.id,
                onActionClick = { onReviewDose(dose) }
            )
        }
        item(key = "adherence-title") { RoutineSectionTitle(text = stringResource(R.string.medication_adherence_title)) }
        item(key = "adherence") { WeeklyAdherenceCard(adherence = uiState.weeklyAdherence) }
    }
}

@Composable
private fun NextDoseBanner(dose: Reminder, now: Instant) {
    val issued = dose.status != ReminderStatus.SCHEDULED
    RoutineBanner(
        iconRes = R.drawable.ic_bell,
        title = if (issued) {
            stringResource(R.string.medication_next_issued_title, dose.displayTitle())
        } else {
            stringResource(R.string.medication_next_title, durationUntil(dose.scheduledTime, now))
        },
        message = if (issued) {
            stringResource(R.string.medication_next_issued_message, DemoSession.CARE_RECIPIENT_FIRST_NAME)
        } else {
            stringResource(R.string.medication_next_message)
        },
        tone = RoutineTone.YELLOW
    )
}

@Composable
private fun DoseItem(dose: Reminder, isNext: Boolean, isBusy: Boolean, onConfirmClick: () -> Unit) {
    val confirmedAt = dose.confirmedAt
    val (state, badgeRes, tone) = when (dose.status) {
        ReminderStatus.CONFIRMED -> Triple(TimelineState.DONE, R.string.medication_status_taken, RoutineTone.MINT)
        ReminderStatus.MISSED -> Triple(TimelineState.MISSED, R.string.medication_status_missed, RoutineTone.ORANGE)
        ReminderStatus.ISSUED -> Triple(TimelineState.CURRENT, R.string.medication_status_pending, RoutineTone.YELLOW)
        ReminderStatus.REISSUED -> Triple(TimelineState.CURRENT, R.string.medication_status_reissued, RoutineTone.ORANGE)
        else -> if (isNext) {
            Triple(TimelineState.CURRENT, R.string.medication_status_next, RoutineTone.YELLOW)
        } else {
            Triple(TimelineState.UPCOMING, R.string.medication_status_scheduled, RoutineTone.MUTED)
        }
    }
    RoutineTimelineItem(
        time = formatClockTime(dose.scheduledTime),
        iconRes = R.drawable.ic_pill,
        title = dose.displayTitle(),
        detail = if (confirmedAt != null) {
            stringResource(R.string.medication_confirmed_at, formatClockTime(confirmedAt))
        } else {
            dose.detailLine()
        },
        badge = stringResource(badgeRes),
        badgeTone = tone,
        state = state,
        actionLabel = if (dose.canBeConfirmed) stringResource(R.string.medication_confirm_action) else null,
        isActionLoading = isBusy,
        onActionClick = onConfirmClick
    )
}

/** "92 % · 22 de 24 dosis confirmadas" over the last seven days. */
@Composable
private fun WeeklyAdherenceCard(adherence: Adherence?) {
    RoutineCard {
        when {
            adherence == null -> Text(
                text = stringResource(R.string.routines_adherence_unavailable),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            !adherence.hasDueReminders -> Text(
                text = stringResource(R.string.medication_adherence_none),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            else -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.routines_percent, adherence.percentage),
                            style = MaterialTheme.typography.dataMetric,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.medication_adherence_detail, adherence.confirmed, adherence.due),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    AdherenceBadge(percentage = adherence.percentage)
                }
                RoutineProgressBar(modifier = Modifier.padding(top = 12.dp), percentage = adherence.percentage)
            }
        }
    }
}

/** "Buen progreso" from 80 %, "Regular" from 50 %, "Requiere atención" below. */
@Composable
fun AdherenceBadge(percentage: Int) {
    when {
        percentage >= 80 -> RoutineBadge(text = stringResource(R.string.routines_adherence_good))
        percentage >= 50 -> RoutineBadge(text = stringResource(R.string.routines_adherence_fair), tone = RoutineTone.YELLOW)
        else -> RoutineBadge(text = stringResource(R.string.routines_adherence_low), tone = RoutineTone.ORANGE)
    }
}

private val previewNow: Instant = Instant.parse("2026-10-08T18:35:00Z")

private fun previewDose(id: String, title: String, minutesFromNow: Long, status: ReminderStatus, confirmed: Boolean = false) = Reminder(
    id = id,
    personUnderCareId = "elena",
    type = ReminderType.MEDICATION,
    title = title,
    dosage = "1 tableta",
    instructions = "Después del almuerzo",
    scheduledTime = previewNow + Duration.ofMinutes(minutesFromNow),
    confirmedAt = if (confirmed) previewNow + Duration.ofMinutes(minutesFromNow + 3) else null,
    status = status
)

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun MedicationContentPreview() {
    GuardianTheme(dynamicColor = false) {
        MedicationContent(
            uiState = MedicationUiState(
                isLoaded = true,
                todayReminders = listOf(
                    previewDose("1", "Omeprazol", -400, ReminderStatus.REISSUED),
                    previewDose("2", "Metformina", -390, ReminderStatus.CONFIRMED, confirmed = true),
                    previewDose("3", "Losartán", 25, ReminderStatus.SCHEDULED),
                    previewDose("4", "Metformina", 360, ReminderStatus.SCHEDULED)
                ),
                weeklyAdherence = Adherence(
                    type = ReminderType.MEDICATION,
                    from = LocalDate.parse("2026-10-02"),
                    to = LocalDate.parse("2026-10-08"),
                    due = 24,
                    confirmed = 22,
                    percentage = 92
                ),
                now = previewNow
            ),
            onBackClick = {},
            onAddDoseClick = {},
            onRetryClick = {},
            onConfirmDose = {},
            onMarkDoseAsMissed = {}
        )
    }
}
