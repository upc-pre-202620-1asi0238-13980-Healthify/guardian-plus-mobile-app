package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.medicationform

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.RecurrenceFrequency
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineFormBody
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineFormSection
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineInfoLine
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSelectField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTextField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTimeField
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.formatTimeOfDay
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.weekdayInitial
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalTime

// Same limit as the platform's ScheduleReminderResource
private const val MAX_TEXT_LENGTH = 255

private val FREQUENCIES = listOf(RecurrenceFrequency.DAILY, RecurrenceFrequency.WEEKLY, RecurrenceFrequency.ONCE)

// Indications of the prototype, plus none
private val INSTRUCTIONS = listOf(
    R.string.medication_instructions_after_lunch,
    R.string.medication_instructions_before_meals,
    R.string.medication_instructions_with_food,
    R.string.medication_instructions_fasting,
    R.string.medication_instructions_none
)

/** "Nueva toma": schedules a medication reminder on the platform. */
@Composable
fun MedicationFormScreen(
    modifier: Modifier = Modifier,
    viewModel: MedicationFormViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSaved: (message: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.savedTime) {
        uiState.savedTime?.let { onSaved(context.getString(R.string.medication_form_saved, formatTimeOfDay(it))) }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            viewModel.onErrorShown()
            snackbarHostState.showSnackbar(it)
        }
    }

    Box(modifier = modifier) {
        MedicationFormContent(isSaving = uiState.isSaving, onBackClick = onBackClick, onSave = viewModel::save)
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun MedicationFormContent(
    modifier: Modifier = Modifier,
    isSaving: Boolean,
    onBackClick: () -> Unit,
    onSave: (
        name: String,
        dosage: String,
        frequency: RecurrenceFrequency,
        days: Set<DayOfWeek>,
        time: LocalTime,
        instructions: String?
    ) -> Unit
) {
    // What the user is typing is purely visual state, so it lives here and not in the ViewModel
    var name by rememberSaveable { mutableStateOf("") }
    var dosage by rememberSaveable { mutableStateOf("") }
    var frequency by rememberSaveable { mutableStateOf(RecurrenceFrequency.DAILY) }
    var days by rememberSaveable { mutableStateOf(setOf<DayOfWeek>()) }
    var time by rememberSaveable { mutableStateOf(LocalTime.of(8, 0)) }
    var instructionsRes by rememberSaveable { mutableStateOf(INSTRUCTIONS.first()) }
    // Errors only show after the first attempt, so an empty form does not start in red
    var showErrors by rememberSaveable { mutableStateOf(false) }

    val nameValid = name.isNotBlank()
    val daysValid = frequency != RecurrenceFrequency.WEEKLY || days.isNotEmpty()
    val instructions = if (instructionsRes == R.string.medication_instructions_none) null else stringResource(instructionsRes)

    Column(modifier = modifier.fillMaxSize()) {
        RoutineTopBar(
            title = stringResource(R.string.medication_form_title),
            subtitle = stringResource(R.string.medication_form_subtitle),
            onBackClick = onBackClick
        )
        RoutineFormBody(
            modifier = Modifier.weight(1f),
            saveLabel = stringResource(R.string.medication_form_save),
            isSaving = isSaving,
            onSaveClick = {
                showErrors = true
                if (nameValid && daysValid) onSave(name, dosage, frequency, days, time, instructions)
            }
        ) {
            RoutineFormSection(title = stringResource(R.string.medication_form_section_medication)) {
                RoutineTextField(
                    label = stringResource(R.string.medication_form_name),
                    value = name,
                    onValueChange = { name = it },
                    placeholder = stringResource(R.string.medication_form_name_hint),
                    errorMessage = if (showErrors && !nameValid) stringResource(R.string.medication_form_name_error) else null,
                    maxLength = MAX_TEXT_LENGTH,
                    capitalization = KeyboardCapitalization.Words
                )
                RoutineTextField(
                    label = stringResource(R.string.medication_form_dosage),
                    value = dosage,
                    onValueChange = { dosage = it },
                    placeholder = stringResource(R.string.medication_form_dosage_hint),
                    maxLength = MAX_TEXT_LENGTH
                )
                RoutineSelectField(
                    label = stringResource(R.string.medication_form_frequency),
                    options = FREQUENCIES,
                    selected = frequency,
                    optionLabel = { stringResource(it.labelRes()) },
                    onSelect = { frequency = it }
                )
                if (frequency == RecurrenceFrequency.WEEKLY) {
                    WeekdayPicker(
                        selected = days,
                        showError = showErrors && !daysValid,
                        onToggle = { day -> days = if (day in days) days - day else days + day }
                    )
                }
            }
            RoutineFormSection(title = stringResource(R.string.medication_form_section_schedule)) {
                RoutineTimeField(label = stringResource(R.string.medication_form_time), value = time, onValueChange = { time = it })
                RoutineSelectField(
                    label = stringResource(R.string.medication_form_instructions),
                    options = INSTRUCTIONS,
                    selected = instructionsRes,
                    optionLabel = { stringResource(it) },
                    onSelect = { instructionsRes = it }
                )
            }
            RoutineFormSection(title = stringResource(R.string.medication_form_section_wristband)) {
                RoutineInfoLine(iconRes = R.drawable.ic_clock, text = stringResource(R.string.medication_form_reissue_note))
            }
        }
    }
}

/** Chips "L M X J V S D" for "Días específicos". */
@Composable
private fun WeekdayPicker(selected: Set<DayOfWeek>, showError: Boolean, onToggle: (DayOfWeek) -> Unit) {
    Column {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DayOfWeek.entries.forEach { day ->
                FilterChip(
                    selected = day in selected,
                    onClick = { onToggle(day) },
                    label = { Text(text = weekdayInitial(day)) },
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
        if (showError) {
            Text(
                text = stringResource(R.string.medication_form_days_error),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun RecurrenceFrequency.labelRes(): Int = when (this) {
    RecurrenceFrequency.DAILY -> R.string.routines_frequency_daily
    RecurrenceFrequency.WEEKLY -> R.string.routines_frequency_weekly
    RecurrenceFrequency.ONCE -> R.string.routines_frequency_once
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun MedicationFormContentPreview() {
    GuardianTheme(dynamicColor = false) {
        MedicationFormContent(isSaving = false, onBackClick = {}, onSave = { _, _, _, _, _, _ -> })
    }
}
