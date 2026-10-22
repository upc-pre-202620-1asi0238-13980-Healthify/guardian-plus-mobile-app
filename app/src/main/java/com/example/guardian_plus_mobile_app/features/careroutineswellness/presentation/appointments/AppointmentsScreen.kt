package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.appointments

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.JetBrainsMono
import com.example.guardian_plus_mobile_app.core.session.LocalCareContext
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineBadge
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineEmptyState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineErrorState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineLoadingState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessageEffect
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSectionTitle
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTone
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBarButton
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.dayLabel
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.displayTitle
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.leadTimeLabel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val monthAbbreviation: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM", Locale.forLanguageTag("es"))

/** "Citas médicas": the next appointment, the later ones and a way to schedule another. */
@Composable
fun AppointmentsScreen(
    modifier: Modifier = Modifier,
    viewModel: AppointmentsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddAppointmentClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    RoutineMessageEffect(uiState.message, snackbarHostState, viewModel::onMessageShown)

    Box(modifier = modifier) {
        AppointmentsContent(
            uiState = uiState,
            onBackClick = onBackClick,
            onAddAppointmentClick = onAddAppointmentClick,
            onRetryClick = viewModel::load,
            onCancelAppointment = viewModel::cancelAppointment
        )
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun AppointmentsContent(
    modifier: Modifier = Modifier,
    uiState: AppointmentsUiState,
    onBackClick: () -> Unit,
    onAddAppointmentClick: () -> Unit,
    onRetryClick: () -> Unit,
    onCancelAppointment: (Reminder) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        RoutineTopBar(
            title = stringResource(R.string.routines_category_appointments),
            subtitle = stringResource(R.string.appointments_subtitle),
            onBackClick = onBackClick,
            action = {
                RoutineTopBarButton(
                    iconRes = R.drawable.ic_plus,
                    contentDescription = stringResource(R.string.appointment_form_title),
                    onClick = onAddAppointmentClick
                )
            }
        )
        Box(modifier = Modifier.weight(1f)) {
            when {
                !uiState.isLoaded && uiState.errorMessage == null -> RoutineLoadingState()
                !uiState.isLoaded -> RoutineErrorState(message = uiState.errorMessage.orEmpty(), onRetryClick = onRetryClick)
                else -> AppointmentsList(
                    uiState = uiState,
                    onAddAppointmentClick = onAddAppointmentClick,
                    onCancelAppointment = onCancelAppointment
                )
            }
        }
    }
}

@Composable
private fun AppointmentsList(
    uiState: AppointmentsUiState,
    onAddAppointmentClick: () -> Unit,
    onCancelAppointment: (Reminder) -> Unit
) {
    val careContext = LocalCareContext.current
    var expandedId by rememberSaveable { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val next = uiState.nextAppointment
        if (next == null) {
            item(key = "empty") {
                RoutineEmptyState(
                    iconRes = R.drawable.ic_calendar,
                    title = stringResource(R.string.appointments_empty_title),
                    message = stringResource(R.string.appointments_empty_message, careContext.careRecipientFirstName),
                    actionLabel = stringResource(R.string.appointments_empty_action),
                    onActionClick = onAddAppointmentClick
                )
            }
        } else {
            item(key = "next-${next.id}") {
                NextAppointmentCard(
                    appointment = next,
                    now = uiState.now,
                    isCancelling = uiState.cancellingId == next.id,
                    onCancelClick = { onCancelAppointment(next) }
                )
            }
        }
        if (uiState.laterAppointments.isNotEmpty()) {
            item(key = "later-title") { RoutineSectionTitle(text = stringResource(R.string.appointments_later_title)) }
            items(uiState.laterAppointments, key = { it.id }) { appointment ->
                LaterAppointmentCard(
                    appointment = appointment,
                    expanded = expandedId == appointment.id,
                    isCancelling = uiState.cancellingId == appointment.id,
                    onClick = { expandedId = if (expandedId == appointment.id) null else appointment.id },
                    onCancelClick = { onCancelAppointment(appointment) }
                )
            }
        }
    }
}

/** Highlighted card of the next appointment: date tile, day and state badges, time, notice and place. */
@Composable
private fun NextAppointmentCard(appointment: Reminder, now: Instant, isCancelling: Boolean, onCancelClick: () -> Unit) {
    RoutineCard {
        Row(verticalAlignment = Alignment.Top) {
            DateTile(at = appointment.scheduledTime, size = 58.dp, dayFontSize = 21.sp)
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    RoutineBadge(
                        text = dayLabel(appointment.scheduledTime, now).replaceFirstChar { it.uppercase() },
                        tone = RoutineTone.YELLOW
                    )
                    AppointmentStatusBadge(appointment)
                }
                Text(
                    text = appointment.displayTitle(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                DetailLine(
                    iconRes = R.drawable.ic_clock,
                    text = stringResource(
                        R.string.appointments_time_and_notice,
                        formatClockTime(appointment.scheduledTime),
                        leadTimeLabel(appointment.leadTimeMinutes)
                    )
                )
                appointment.location?.let { DetailLine(iconRes = R.drawable.ic_map_pin, text = it) }
                appointment.instructions?.let { DetailLine(iconRes = R.drawable.ic_info, text = it) }
            }
        }
        CancelButton(isCancelling = isCancelling, onClick = onCancelClick)
    }
}

/** Collapsed row of a later appointment; tapping it shows the notice and a way to cancel it. */
@Composable
private fun LaterAppointmentCard(
    appointment: Reminder,
    expanded: Boolean,
    isCancelling: Boolean,
    onClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DateTile(at = appointment.scheduledTime, size = 46.dp, dayFontSize = 16.sp)
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)) {
                    Text(text = appointment.displayTitle(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = listOfNotNull(formatClockTime(appointment.scheduledTime), appointment.location).joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(if (expanded) 90f else 0f)
                )
            }
            if (expanded) {
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(top = 11.dp, bottom = 10.dp))
                DetailLine(iconRes = R.drawable.ic_bell, text = leadTimeLabel(appointment.leadTimeMinutes))
                appointment.instructions?.let { DetailLine(iconRes = R.drawable.ic_info, text = it) }
                CancelButton(isCancelling = isCancelling, onClick = onCancelClick)
            }
        }
    }
}

@Composable
private fun AppointmentStatusBadge(appointment: Reminder) {
    if (appointment.status == ReminderStatus.SCHEDULED) {
        RoutineBadge(text = stringResource(R.string.appointments_status_scheduled))
    } else {
        RoutineBadge(text = stringResource(R.string.appointments_status_notified), tone = RoutineTone.BLUE)
    }
}

@Composable
private fun CancelButton(isCancelling: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        enabled = !isCancelling,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        if (isCancelling) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        } else {
            Text(text = stringResource(R.string.appointments_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Mint square with the day number and the month, e.g. "24 ABR". */
@Composable
private fun DateTile(at: Instant, size: Dp, dayFontSize: TextUnit) {
    val date = at.atZone(ZoneId.systemDefault())
    Column(
        modifier = Modifier
            .width(size)
            .height(size + 2.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "%02d".format(date.dayOfMonth),
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold, fontSize = dayFontSize),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = monthAbbreviation.format(date).replace(".", "").uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun DetailLine(@DrawableRes iconRes: Int, text: String) {
    Row(
        modifier = Modifier.padding(top = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(15.dp)
        )
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private val previewNow: Instant = Instant.parse("2026-10-08T18:35:00Z")

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun AppointmentsContentPreview() {
    GuardianTheme(dynamicColor = false) {
        AppointmentsContent(
            uiState = AppointmentsUiState(
                isLoaded = true,
                appointments = listOf(
                    Reminder(
                        id = "1",
                        personUnderCareId = "elena",
                        type = ReminderType.APPOINTMENT,
                        title = "Control de cardiología",
                        location = "Clínica San Gabriel · Consultorio 204",
                        scheduledTime = previewNow + Duration.ofHours(16),
                        leadTimeMinutes = 60,
                        status = ReminderStatus.SCHEDULED
                    ),
                    Reminder(
                        id = "2",
                        personUnderCareId = "elena",
                        type = ReminderType.APPOINTMENT,
                        title = "Medicina general",
                        location = "Centro de Salud Norte",
                        scheduledTime = previewNow + Duration.ofDays(14),
                        leadTimeMinutes = 60,
                        status = ReminderStatus.SCHEDULED
                    )
                ),
                now = previewNow
            ),
            onBackClick = {},
            onAddAppointmentClick = {},
            onRetryClick = {},
            onCancelAppointment = {}
        )
    }
}
