package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.appointmentform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.session.LocalCareContext
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineBanner
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineDateField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineFormBody
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineFormSection
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSelectField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTextField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTimeField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTone
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.leadTimeLabel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

// Same limit as the platform's ScheduleReminderResource
private const val MAX_TEXT_LENGTH = 255

// "Recordar": 1 hora, 2 horas or 1 día antes, as lead time in minutes
private val LEAD_TIMES = listOf(60, 120, 1_440)

/** "Nueva cita": schedules a medical appointment and its preventive notice. */
@Composable
fun AppointmentFormScreen(
    modifier: Modifier = Modifier,
    viewModel: AppointmentFormViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSaved: (message: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSaved(context.getString(R.string.appointment_form_saved))
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            viewModel.onErrorShown()
            snackbarHostState.showSnackbar(it)
        }
    }

    Box(modifier = modifier) {
        AppointmentFormContent(
            now = viewModel.now(),
            isSaving = uiState.isSaving,
            onBackClick = onBackClick,
            onSave = viewModel::save
        )
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun AppointmentFormContent(
    modifier: Modifier = Modifier,
    now: Instant,
    isSaving: Boolean,
    onBackClick: () -> Unit,
    onSave: (specialty: String, dateTime: LocalDateTime, location: String, leadTimeMinutes: Int) -> Unit
) {
    val careContext = LocalCareContext.current
    val today = now.atZone(ZoneId.systemDefault()).toLocalDate()
    var specialty by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(today.plusDays(1)) }
    var time by rememberSaveable { mutableStateOf(LocalTime.of(10, 0)) }
    var location by rememberSaveable { mutableStateOf("") }
    var leadTime by rememberSaveable { mutableStateOf(LEAD_TIMES.first()) }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    val specialtyValid = specialty.isNotBlank()
    val dateTime = LocalDateTime.of(date, time)
    val inFuture = dateTime.atZone(ZoneId.systemDefault()).toInstant().isAfter(now)

    Column(modifier = modifier.fillMaxSize()) {
        RoutineTopBar(
            title = stringResource(R.string.appointment_form_title),
            subtitle = stringResource(R.string.appointment_form_subtitle),
            onBackClick = onBackClick
        )
        RoutineFormBody(
            modifier = Modifier.weight(1f),
            saveLabel = stringResource(R.string.appointment_form_save),
            isSaving = isSaving,
            onSaveClick = {
                showErrors = true
                if (specialtyValid && inFuture) onSave(specialty, dateTime, location, leadTime)
            }
        ) {
            RoutineFormSection {
                RoutineTextField(
                    label = stringResource(R.string.appointment_form_specialty),
                    value = specialty,
                    onValueChange = { specialty = it },
                    placeholder = stringResource(R.string.appointment_form_specialty_hint),
                    errorMessage = if (showErrors && !specialtyValid) stringResource(R.string.appointment_form_specialty_error) else null,
                    maxLength = MAX_TEXT_LENGTH
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RoutineDateField(
                        modifier = Modifier.weight(1.3f),
                        label = stringResource(R.string.appointment_form_date),
                        value = date,
                        minDate = today,
                        onValueChange = { date = it }
                    )
                    RoutineTimeField(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.appointment_form_time),
                        value = time,
                        errorMessage = if (showErrors && !inFuture) stringResource(R.string.appointment_form_time_error) else null,
                        onValueChange = { time = it }
                    )
                }
                RoutineTextField(
                    label = stringResource(R.string.appointment_form_location),
                    value = location,
                    onValueChange = { location = it },
                    placeholder = stringResource(R.string.appointment_form_location_hint),
                    maxLength = MAX_TEXT_LENGTH,
                    capitalization = KeyboardCapitalization.Words
                )
                RoutineSelectField(
                    label = stringResource(R.string.appointment_form_remind),
                    options = LEAD_TIMES,
                    selected = leadTime,
                    optionLabel = { leadTimeLabel(it) },
                    onSelect = { leadTime = it }
                )
            }
            RoutineBanner(
                iconRes = R.drawable.ic_bell,
                title = stringResource(R.string.appointment_form_notice_title),
                message = stringResource(R.string.appointment_form_notice_message, careContext.careRecipientFirstName),
                tone = RoutineTone.MINT
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun AppointmentFormContentPreview() {
    GuardianTheme(dynamicColor = false) {
        AppointmentFormContent(
            now = LocalDate.parse("2026-10-08").atStartOfDay(ZoneId.systemDefault()).toInstant(),
            isSaving = false,
            onBackClick = {},
            onSave = { _, _, _, _ -> }
        )
    }
}
