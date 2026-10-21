package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val fieldDate: DateTimeFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM yyyy", Locale.forLanguageTag("es"))
private val fieldTime: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/** Small muted label above a form field, as in the prototype. */
@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/** Free-text field with its label above. */
@Composable
fun RoutineTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    errorMessage: String? = null,
    maxLength: Int = 255,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        FieldLabel(text = label)
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.take(maxLength)) },
            placeholder = placeholder?.let { { Text(text = it) } },
            isError = errorMessage != null,
            supportingText = errorMessage?.let { { Text(text = it) } },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            keyboardOptions = KeyboardOptions(capitalization = capitalization, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Read-only field that opens a picker or a menu when tapped. */
@Composable
private fun RoutinePickerBox(
    value: String,
    @DrawableRes iconRes: Int,
    isError: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/** Closed list of options ("Todos los días", "1 hora antes"…), like the prototype's select. */
@Composable
fun <T> RoutineSelectField(
    modifier: Modifier = Modifier,
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: @Composable (T) -> String,
    enabled: Boolean = true,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        FieldLabel(text = label)
        Box {
            RoutinePickerBox(
                value = optionLabel(selected),
                iconRes = R.drawable.ic_arrow_down,
                onClick = { if (enabled) expanded = true }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = optionLabel(option)) },
                        onClick = {
                            expanded = false
                            if (option != selected) onSelect(option)
                        }
                    )
                }
            }
        }
    }
}

/** Time of day in 24 h, picked on the Material clock. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineTimeField(
    modifier: Modifier = Modifier,
    label: String,
    value: LocalTime,
    errorMessage: String? = null,
    onValueChange: (LocalTime) -> Unit
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        FieldLabel(text = label)
        RoutinePickerBox(
            value = fieldTime.format(value),
            iconRes = R.drawable.ic_clock,
            isError = errorMessage != null,
            onClick = { showPicker = true }
        )
        errorMessage?.let {
            Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        }
    }

    if (showPicker) {
        val state = rememberTimePickerState(initialHour = value.hour, initialMinute = value.minute, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onValueChange(LocalTime.of(state.hour, state.minute))
                    showPicker = false
                }) {
                    Text(text = stringResource(R.string.routines_form_pick_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text(text = stringResource(R.string.action_cancel)) }
            },
            text = { TimePicker(state = state) }
        )
    }
}

/** Calendar day from [minDate] on, for appointments. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDateField(
    modifier: Modifier = Modifier,
    label: String,
    value: LocalDate,
    minDate: LocalDate,
    onValueChange: (LocalDate) -> Unit
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        FieldLabel(text = label)
        RoutinePickerBox(value = fieldDate.format(value), iconRes = R.drawable.ic_calendar, onClick = { showPicker = true })
    }

    if (showPicker) {
        // The picker speaks UTC milliseconds at midnight
        val minMillis = minDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val state = rememberDatePickerState(
            initialSelectedDateMillis = value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= minMillis
                override fun isSelectableYear(year: Int): Boolean = year >= minDate.year
            }
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { millis ->
                            onValueChange(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                        }
                        showPicker = false
                    },
                    enabled = state.selectedDateMillis != null
                ) {
                    Text(text = stringResource(R.string.routines_form_pick_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text(text = stringResource(R.string.action_cancel)) }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoutineFormFieldsPreview() {
    GuardianTheme(dynamicColor = false) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RoutineTextField(label = "Nombre", value = "Losartán", onValueChange = {})
            RoutineSelectField(
                label = "Frecuencia",
                options = listOf("Todos los días", "Una sola vez"),
                selected = "Todos los días",
                optionLabel = { it },
                onSelect = {}
            )
            RoutineTimeField(label = "Hora de la toma", value = LocalTime.of(14, 0), onValueChange = {})
        }
    }
}
