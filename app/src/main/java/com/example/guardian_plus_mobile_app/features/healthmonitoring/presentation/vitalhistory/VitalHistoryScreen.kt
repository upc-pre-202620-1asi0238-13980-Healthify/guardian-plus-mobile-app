package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.ErrorState
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.HistoryPeriod
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.VitalFilter
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.VitalFilterOption
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.pdf.exportHealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.component.PeriodAverageCard
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.component.ExportRecordSheet
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.component.ReadingItem
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.component.VitalTypeFilterRow
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.component.WeeklyReportSheet
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** "Salud › Historial": the chosen period of one vital sign and the latest readings of all of them. */
@Composable
fun VitalHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: VitalHistoryViewModel = hiltViewModel(),
    filter: VitalFilter = VitalFilter()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // The ViewModel decides whether the new filter needs another period from the platform
    LaunchedEffect(filter) { viewModel.applyFilter(filter) }

    val exportFailed = stringResource(R.string.report_export_failed)
    LaunchedEffect(uiState.reportToExport) {
        uiState.reportToExport?.let { request ->
            if (!context.exportHealthReport(request.report, DemoSession.CARE_RECIPIENT_NAME, request.metrics)) {
                Toast.makeText(context, exportFailed, Toast.LENGTH_LONG).show()
            }
            viewModel.onReportExported()
        }
    }

    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.onActionMessageShown()
        }
    }

    VitalHistoryContent(
        modifier = modifier,
        uiState = uiState,
        onSelectType = viewModel::selectType,
        onRetryClick = viewModel::load,
        onExportClick = viewModel::openExportSheet,
        onWeeklyReportClick = viewModel::openWeeklyReport
    )

    if (uiState.isExportSheetOpen) {
        ExportRecordSheet(
            today = uiState.today,
            careRecipientName = DemoSession.CARE_RECIPIENT_NAME,
            emptyRanges = uiState.emptyRanges,
            isExporting = uiState.busyAction == ReportAction.EXPORT_PDF,
            onExport = viewModel::exportRecord,
            onDismiss = viewModel::closeExportSheet
        )
    }

    uiState.weeklyReport?.let { weeklyReport ->
        WeeklyReportSheet(
            weeklyReport = weeklyReport,
            careRecipientName = DemoSession.CARE_RECIPIENT_NAME,
            onDismiss = viewModel::closeWeeklyReport
        )
    }
}

@Composable
fun VitalHistoryContent(
    modifier: Modifier = Modifier,
    uiState: VitalHistoryUiState,
    onSelectType: (VitalSignType) -> Unit,
    onRetryClick: () -> Unit,
    onExportClick: () -> Unit,
    onWeeklyReportClick: () -> Unit
) {
    when {
        uiState.isLoading && uiState.readings.isEmpty() -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        uiState.errorMessage != null && uiState.readings.isEmpty() -> ErrorState(
            modifier = modifier,
            message = uiState.errorMessage,
            onRetryClick = onRetryClick
        )

        // An empty period still shows the chips and the card with "Sin lecturas esta semana"
        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "filters") {
                VitalTypeFilterRow(types = uiState.filter.types, selected = uiState.chartType, onSelect = onSelectType)
            }
            item(key = "average") {
                val type = uiState.chartType
                PeriodAverageCard(
                    type = type,
                    period = uiState.period,
                    average = uiState.average(type),
                    min = uiState.min(type),
                    max = uiState.max(type),
                    values = uiState.chartValues(type),
                    days = uiState.days
                )
            }
            items(uiState.recentReadings, key = { it.id }) { reading ->
                ReadingItem(
                    reading = reading,
                    diastolic = uiState.diastolicFor(reading),
                    withinRange = uiState.isWithinRange(reading),
                    today = uiState.today,
                    zone = uiState.zone
                )
            }
            item(key = "actions") {
                ReportActions(
                    busyAction = uiState.busyAction,
                    onExportClick = onExportClick,
                    onWeeklyReportClick = onWeeklyReportClick
                )
            }
        }
    }
}

@Composable
private fun ReportActions(busyAction: ReportAction?, onExportClick: () -> Unit, onWeeklyReportClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onExportClick,
            enabled = busyAction == null,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            if (busyAction == ReportAction.EXPORT_PDF) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(18.dp)
                )
                Text(text = stringResource(R.string.health_export_pdf))
            }
        }
        Button(
            onClick = onWeeklyReportClick,
            enabled = busyAction == null,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if (busyAction == ReportAction.WEEKLY_REPORT) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = stringResource(R.string.health_weekly_report))
            }
        }
    }
}

private val previewToday: LocalDate = LocalDate.parse("2026-10-06")
private val previewZone: ZoneId = ZoneId.of("America/Lima")

private fun previewReadings(): List<VitalSignReading> {
    val heartRates = listOf(75.0, 77.0, 73.0, 80.0, 76.0, 78.0, 78.0)
    val week = heartRates.mapIndexed { index, value ->
        val at = previewToday.minusDays(6L - index).atTime(14, 32).atZone(previewZone).toInstant()
        VitalSignReading("hr$index", VitalSignType.HR, value, at)
    }
    val yesterday: Instant = previewToday.minusDays(1).atTime(22, 4).atZone(previewZone).toInstant()
    val todayMorning: Instant = previewToday.atTime(9, 15).atZone(previewZone).toInstant()
    return listOf(
        VitalSignReading("sys", VitalSignType.BP_SYS, 146.0, yesterday),
        VitalSignReading("dia", VitalSignType.BP_DIA, 80.0, yesterday),
        VitalSignReading("spo2", VitalSignType.SPO2, 98.0, todayMorning)
    ) + week
}

private val previewState = VitalHistoryUiState(
    readings = previewReadings().sortedBy { it.measuredAt },
    ranges = mapOf(
        VitalSignType.HR to 60.0..100.0,
        VitalSignType.BP_SYS to 90.0..140.0,
        VitalSignType.BP_DIA to 60.0..90.0,
        VitalSignType.SPO2 to 92.0..100.0
    ),
    today = previewToday,
    zone = previewZone
)

@Composable
private fun VitalHistoryContentPreview(uiState: VitalHistoryUiState) {
    GuardianTheme(dynamicColor = false) {
        VitalHistoryContent(
            uiState = uiState,
            onSelectType = {},
            onRetryClick = {},
            onExportClick = {},
            onWeeklyReportClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun VitalHistoryContentWeekPreview() {
    VitalHistoryContentPreview(previewState)
}

@Preview(showBackground = true, heightDp = 700)
@Composable
private fun VitalHistoryContentEmptyPreview() {
    VitalHistoryContentPreview(VitalHistoryUiState(today = previewToday, zone = previewZone))
}

@Preview(showBackground = true)
@Composable
private fun VitalHistoryContentErrorPreview() {
    VitalHistoryContentPreview(VitalHistoryUiState(errorMessage = "No se pudo conectar con el servidor. Revisa tu conexión."))
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun VitalHistoryContentDayPreview() {
    VitalHistoryContentPreview(
        previewState.copy(
            filter = VitalFilter().toggle(VitalFilterOption.HEART_RATE).toggle(VitalFilterOption.DAY),
            period = HistoryPeriod.DAY
        )
    )
}
