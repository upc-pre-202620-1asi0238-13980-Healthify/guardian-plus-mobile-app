package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Adherence
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.StatusChip
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReportType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.StabilityIndex
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.title
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.SummaryRow
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.generatedAtText
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.periodText
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.previewHealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.WeeklyReport

/** "Reporte semanal" (US24): how stable the week was, the alerts and medication around it, and each sign's behaviour. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReportSheet(
    modifier: Modifier = Modifier,
    weeklyReport: WeeklyReport,
    careRecipientName: String,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        WeeklyReportContent(weeklyReport = weeklyReport, careRecipientName = careRecipientName, onClose = onDismiss)
    }
}

@Composable
fun WeeklyReportContent(
    modifier: Modifier = Modifier,
    weeklyReport: WeeklyReport,
    careRecipientName: String,
    onClose: () -> Unit
) {
    val report = weeklyReport.report

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.weekly_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$careRecipientName · ${report.periodText()}",
                    style = MaterialTheme.typography.dataLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            SheetCloseButton(onClick = onClose)
        }

        Indicators(weeklyReport = weeklyReport, modifier = Modifier.padding(top = 8.dp))

        SheetSectionLabel(text = stringResource(R.string.weekly_parameters), modifier = Modifier.padding(top = 12.dp))
        weeklyReport.parameters.forEach { row -> ParameterRow(row = row) }

        if (weeklyReport.recurrentParameters.isNotEmpty()) {
            RecurrentNotice(rows = weeklyReport.recurrentParameters, modifier = Modifier.padding(top = 8.dp))
        }

        Footer(
            generatedAt = report.generatedAtText(),
            automatic = report.reportType == HealthReportType.WEEKLY_AUTOMATIC,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun Indicators(modifier: Modifier = Modifier, weeklyReport: WeeklyReport) {
    val adherence = weeklyReport.medicationAdherence
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Indicator(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.weekly_stability),
                value = weeklyReport.stabilityPercentage?.toString(),
                suffix = "%",
                caption = stringResource(R.string.weekly_stability_caption)
            )
            VerticalDivider(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.outline)
            Indicator(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.weekly_alerts),
                value = weeklyReport.alertsTriggered?.toString(),
                suffix = null,
                caption = stringResource(R.string.weekly_alerts_caption)
            )
            VerticalDivider(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.outline)
            // With no dose scheduled in the week there is no adherence to speak of, so it reads "--"
            Indicator(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.weekly_adherence),
                value = adherence?.takeIf { it.hasDueReminders }?.percentage?.toString(),
                suffix = "%",
                caption = stringResource(
                    if (adherence != null && !adherence.hasDueReminders) R.string.weekly_adherence_none else R.string.weekly_adherence_caption
                )
            )
        }
    }
}

@Composable
private fun Indicator(modifier: Modifier, label: String, value: String?, suffix: String?, caption: String) {
    Column(
        modifier = modifier.padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 6.dp)) {
            Text(
                text = value ?: "--",
                style = MaterialTheme.typography.dataMetric.copy(fontSize = 28.sp, lineHeight = 32.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (value != null && suffix != null) {
                Text(
                    text = suffix,
                    style = MaterialTheme.typography.dataLabel,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
        Text(
            text = caption,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun ParameterRow(row: SummaryRow) {
    val scheme = MaterialTheme.colorScheme
    val recurrent = row.stability == StabilityIndex.RECURRENT
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = if (recurrent) scheme.errorContainer else scheme.surface,
        border = BorderStroke(1.dp, if (recurrent) scheme.error.copy(alpha = 0.35f) else scheme.outline)
    ) {
        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = row.type.title, style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp), color = scheme.onSurface)
                Text(
                    text = if (row.outOfRangeCount == 0) {
                        stringResource(R.string.weekly_no_anomalies)
                    } else {
                        pluralStringResource(R.plurals.weekly_anomalies, row.outOfRangeCount, row.outOfRangeCount)
                    },
                    style = MaterialTheme.typography.dataLabel,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            val (container, content) = row.stability.chipColors()
            StatusChip(text = stringResource(row.stability.chipLabelRes()), container = container, content = content)
        }
    }
}

@Composable
private fun RecurrentNotice(modifier: Modifier = Modifier, rows: List<SummaryRow>) {
    val scheme = MaterialTheme.colorScheme
    val sentences = rows.map { row ->
        pluralStringResource(R.plurals.weekly_recurrent_sentence, row.outOfRangeCount, row.type.title, row.outOfRangeCount)
    }
    val specialties = rows.map { stringResource(it.type.specialtyRes()) }.distinct()
    val body = sentences.joinToString(" ") + " " +
        stringResource(R.string.weekly_recurrent_advice, specialties.joinToString(stringResource(R.string.weekly_and)))

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = scheme.errorContainer,
        border = BorderStroke(1.dp, scheme.error.copy(alpha = 0.35f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = "!",
                style = MaterialTheme.typography.titleMedium,
                color = scheme.error,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column {
                Text(
                    text = pluralStringResource(R.plurals.weekly_recurrent_title, rows.size),
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
                    color = scheme.error
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.error.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun Footer(modifier: Modifier = Modifier, generatedAt: String, automatic: Boolean) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(if (automatic) R.string.weekly_generated_automatic else R.string.weekly_generated_on_demand, generatedAt),
            style = MaterialTheme.typography.dataLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(if (automatic) R.string.weekly_story_automatic else R.string.weekly_story_on_demand),
            style = MaterialTheme.typography.dataLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/** Rounded square "×" of the prototype's sheets. */
@Composable
fun SheetCloseButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_x),
            contentDescription = stringResource(R.string.health_filter_close),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(14.dp)
        )
    }
}

/** "COMPORTAMIENTO POR PARÁMETRO", "PERÍODO DE TIEMPO"…: the spaced capitals that open each block of a sheet. */
@Composable
fun SheetSectionLabel(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

// "Recurrente" is the one alarm of the sheet, so it is the only solid chip
@Composable
private fun StabilityIndex.chipColors(): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when (this) {
        StabilityIndex.STABLE -> scheme.primaryContainer to scheme.onPrimaryContainer
        StabilityIndex.UNSTABLE -> scheme.tertiaryContainer to scheme.tertiary
        StabilityIndex.RECURRENT -> scheme.error to scheme.onError
    }
}

private fun StabilityIndex.chipLabelRes(): Int = when (this) {
    StabilityIndex.STABLE -> R.string.report_stability_stable
    StabilityIndex.UNSTABLE -> R.string.reading_observation
    StabilityIndex.RECURRENT -> R.string.report_stability_recurrent
}

// Who should look at a sign that keeps leaving its range
private fun VitalSignType.specialtyRes(): Int = when (this) {
    VitalSignType.HR, VitalSignType.BP_SYS, VitalSignType.BP_DIA -> R.string.specialty_cardiology
    VitalSignType.SPO2, VitalSignType.RESP_RATE -> R.string.specialty_pulmonology
    VitalSignType.TEMP -> R.string.specialty_general_medicine
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun WeeklyReportContentPreview() {
    val report = previewHealthReport(reportType = HealthReportType.WEEKLY_AUTOMATIC).let { base ->
        base.copy(summaries = base.summaries.map { if (it.type == VitalSignType.BP_SYS) it.copy(outOfRangeCount = 4, stability = StabilityIndex.RECURRENT) else it })
    }
    GuardianTheme(dynamicColor = false) {
        WeeklyReportContent(
            weeklyReport = WeeklyReport(
                report = report,
                alertsTriggered = 7,
                medicationAdherence = Adherence(ReminderType.MEDICATION, report.periodStart, report.periodEnd, 22, 20, 91)
            ),
            careRecipientName = "Elena Rojas",
            onClose = {}
        )
    }
}
