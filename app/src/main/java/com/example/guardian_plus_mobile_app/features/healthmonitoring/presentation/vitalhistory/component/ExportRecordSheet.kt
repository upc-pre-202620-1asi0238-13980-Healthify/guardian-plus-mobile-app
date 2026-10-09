package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.displayedVitalTypes
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.title
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.periodText
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.ExportRange
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private enum class ExportPeriod {
    LAST_30_DAYS,
    LAST_7_DAYS,
    CUSTOM
}

/** "Exportar expediente": the days and the signs that go into the PDF of the person's record. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportRecordSheet(
    modifier: Modifier = Modifier,
    today: LocalDate,
    careRecipientName: String,
    emptyRanges: Set<ExportRange>,
    isExporting: Boolean,
    onExport: (ExportRange, Set<VitalSignType>) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        modifier = modifier
    ) {
        ExportRecordForm(
            today = today,
            careRecipientName = careRecipientName,
            emptyRanges = emptyRanges,
            isExporting = isExporting,
            onExport = onExport,
            onClose = onDismiss
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExportRecordForm(
    modifier: Modifier = Modifier,
    today: LocalDate,
    careRecipientName: String,
    emptyRanges: Set<ExportRange>,
    isExporting: Boolean,
    onExport: (ExportRange, Set<VitalSignType>) -> Unit,
    onClose: () -> Unit
) {
    // What is being picked is purely visual until "Generar", so it lives here; dates as ISO text to be saveable
    var period by rememberSaveable { mutableStateOf(ExportPeriod.LAST_30_DAYS) }
    var customStart by rememberSaveable { mutableStateOf<String?>(null) }
    var customEnd by rememberSaveable { mutableStateOf<String?>(null) }
    var metricNames by rememberSaveable { mutableStateOf(displayedVitalTypes.map { it.name }) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val metrics = displayedVitalTypes.filter { it.name in metricNames }.toSet()
    val custom = if (customStart != null && customEnd != null) ExportRange(LocalDate.parse(customStart), LocalDate.parse(customEnd)) else null
    val ranges = mapOf(
        ExportPeriod.LAST_30_DAYS to ExportRange(today.minusDays(29), today),
        ExportPeriod.LAST_7_DAYS to ExportRange(today.minusDays(6), today),
        ExportPeriod.CUSTOM to custom
    )
    val selectedRange = ranges[period]

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, top = 24.dp)
            .navigationBarsPadding()
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.export_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.export_subtitle, careRecipientName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            SheetCloseButton(onClick = onClose)
        }

        SheetSectionLabel(text = stringResource(R.string.export_period), modifier = Modifier.padding(top = 16.dp))
        PeriodOption(
            title = stringResource(R.string.export_last_30_days),
            range = ranges.getValue(ExportPeriod.LAST_30_DAYS),
            isEmpty = ranges.getValue(ExportPeriod.LAST_30_DAYS) in emptyRanges,
            selected = period == ExportPeriod.LAST_30_DAYS,
            onClick = { period = ExportPeriod.LAST_30_DAYS }
        )
        PeriodOption(
            title = stringResource(R.string.export_last_7_days),
            range = ranges.getValue(ExportPeriod.LAST_7_DAYS),
            isEmpty = ranges.getValue(ExportPeriod.LAST_7_DAYS) in emptyRanges,
            selected = period == ExportPeriod.LAST_7_DAYS,
            onClick = { period = ExportPeriod.LAST_7_DAYS }
        )
        // Picking "Personalizado" always asks for the dates, so a previous range can be changed
        PeriodOption(
            title = stringResource(R.string.export_custom),
            range = custom,
            isEmpty = custom != null && custom in emptyRanges,
            selected = period == ExportPeriod.CUSTOM,
            onClick = { showDatePicker = true }
        )

        SheetSectionLabel(text = stringResource(R.string.export_metrics), modifier = Modifier.padding(top = 28.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            displayedVitalTypes.forEach { type ->
                MetricChip(
                    text = type.title,
                    selected = type in metrics,
                    onClick = {
                        metricNames = if (type in metrics) metricNames - type.name else metricNames + type.name
                    }
                )
            }
        }

        Button(
            onClick = { selectedRange?.let { onExport(it, metrics) } },
            enabled = selectedRange != null && metrics.isNotEmpty() && !isExporting,
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(56.dp)
        ) {
            if (isExporting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(18.dp)
                )
                Text(text = stringResource(R.string.export_generate), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    if (showDatePicker) {
        CustomRangeDialog(
            today = today,
            initial = custom,
            onConfirm = { range ->
                customStart = range.start.toString()
                customEnd = range.end.toString()
                period = ExportPeriod.CUSTOM
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun PeriodOption(title: String, range: ExportRange?, isEmpty: Boolean, selected: Boolean, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val noData = stringResource(R.string.export_no_data)
    val subtitle = buildAnnotatedString {
        if (range == null) {
            append(stringResource(R.string.export_pick_dates))
        } else if (isEmpty) {
            // The whole line turns orange, as in the prototype, so the empty range is noticed
            withStyle(SpanStyle(color = scheme.tertiary)) { append("${periodText(range.start, range.end)} · $noData") }
        } else {
            append(periodText(range.start, range.end))
        }
    }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = if (selected) scheme.primaryContainer.copy(alpha = 0.45f) else scheme.background,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) scheme.primary else scheme.outline)
    ) {
        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            SelectionBox(selected = selected)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp), color = scheme.onSurface)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.dataLabel,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

// Rounded square with a dot, the prototype's single-choice mark
@Composable
private fun SelectionBox(selected: Boolean) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(22.dp)
            .background(if (selected) scheme.primary else scheme.surface, MaterialTheme.shapes.extraSmall)
            .border(1.5.dp, if (selected) scheme.primary else scheme.outline, MaterialTheme.shapes.extraSmall),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(scheme.onPrimary, CircleShape)
            )
        }
    }
}

@Composable
private fun MetricChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = if (selected) scheme.primaryContainer else scheme.surface,
        border = if (selected) null else BorderStroke(1.dp, scheme.outline)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.dataLabel,
            color = if (selected) scheme.onPrimaryContainer else scheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomRangeDialog(today: LocalDate, initial: ExportRange?, onConfirm: (ExportRange) -> Unit, onDismiss: () -> Unit) {
    // The picker speaks UTC milliseconds at midnight; the record has no future to export
    val todayMillis = today.toEpochMillis()
    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initial?.start?.toEpochMillis(),
        initialSelectedEndDateMillis = initial?.end?.toEpochMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= todayMillis
            override fun isSelectableYear(year: Int): Boolean = year <= today.year
        }
    )
    val start = state.selectedStartDateMillis
    val end = state.selectedEndDateMillis

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { if (start != null && end != null) onConfirm(ExportRange(start.toLocalDate(), end.toLocalDate())) },
                enabled = start != null && end != null
            ) {
                Text(text = stringResource(R.string.export_pick_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(R.string.action_cancel)) }
        }
    ) {
        DateRangePicker(state = state, modifier = Modifier.height(480.dp))
    }
}

private fun LocalDate.toEpochMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ExportRecordFormPreview() {
    val today = LocalDate.parse("2025-01-29")
    GuardianTheme(dynamicColor = false) {
        ExportRecordForm(
            today = today,
            careRecipientName = "Elena Rojas",
            emptyRanges = emptySet(),
            isExporting = false,
            onExport = { _, _ -> },
            onClose = {}
        )
    }
}
