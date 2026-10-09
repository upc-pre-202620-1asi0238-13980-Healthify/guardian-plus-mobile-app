package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.DetailTopBar
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReportType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.ErrorState
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.component.EarlierReportItem
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.component.ReportOverviewCard
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.component.SummaryCard
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.pdf.exportHealthReport
import java.time.LocalDate
import kotlinx.coroutines.launch

/** A health report compiled by the platform, and the earlier ones of the same person. */
@Composable
fun HealthReportScreen(
    modifier: Modifier = Modifier,
    reportId: String,
    viewModel: HealthReportViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onOpenReport: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // The report is already on screen, so exporting is only file work and stays in the UI
    var isExporting by rememberSaveable { mutableStateOf(false) }
    val exportFailed = stringResource(R.string.report_export_failed)

    LaunchedEffect(reportId) {
        viewModel.load(reportId)
    }

    HealthReportContent(
        modifier = modifier,
        uiState = uiState,
        isExporting = isExporting,
        onBackClick = onBackClick,
        onRetryClick = { viewModel.load(reportId) },
        onExportClick = {
            val report = uiState.report
            if (report != null && !isExporting) {
                scope.launch {
                    isExporting = true
                    if (!context.exportHealthReport(report, DemoSession.CARE_RECIPIENT_NAME)) {
                        Toast.makeText(context, exportFailed, Toast.LENGTH_LONG).show()
                    }
                    isExporting = false
                }
            }
        },
        onOpenReport = onOpenReport
    )
}

@Composable
fun HealthReportContent(
    modifier: Modifier = Modifier,
    uiState: HealthReportUiState,
    isExporting: Boolean = false,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onExportClick: () -> Unit,
    onOpenReport: (String) -> Unit
) {
    val report = uiState.report
    val scheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxSize()) {
        DetailTopBar(
            title = stringResource(R.string.report_title),
            subtitle = DemoSession.CARE_RECIPIENT_NAME,
            statusLabel = report?.let { stringResource(if (it.clinicallyStable) R.string.report_stable else R.string.reading_observation) },
            statusContainer = if (report?.clinicallyStable == true) scheme.primaryContainer else scheme.noticeContainer,
            statusContent = if (report?.clinicallyStable == true) scheme.onPrimaryContainer else scheme.onNoticeContainer,
            onBackClick = onBackClick
        )

        when {
            report == null && uiState.errorMessage != null -> ErrorState(message = uiState.errorMessage, onRetryClick = onRetryClick)

            report == null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "overview") { ReportOverviewCard(report = report) }
                item(key = "vitals-title") { SectionHeading(text = stringResource(R.string.report_vital_signs)) }
                items(report.summaryRows(), key = { it.type.name }) { row -> SummaryCard(row = row) }
                item(key = "export") {
                    OutlinedButton(
                        onClick = onExportClick,
                        enabled = !isExporting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        if (isExporting) {
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
                }
                if (uiState.earlierReports.isNotEmpty()) {
                    item(key = "earlier-title") { SectionHeading(text = stringResource(R.string.report_earlier)) }
                    items(uiState.earlierReports, key = { it.id }) { earlier ->
                        EarlierReportItem(report = earlier, onClick = { onOpenReport(earlier.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun HealthReportContentPreview() {
    GuardianTheme(dynamicColor = false) {
        HealthReportContent(
            uiState = HealthReportUiState(
                report = previewHealthReport(),
                earlierReports = listOf(
                    previewHealthReport(id = "w1", periodEnd = LocalDate.parse("2026-10-04"), reportType = HealthReportType.WEEKLY_AUTOMATIC)
                )
            ),
            onBackClick = {},
            onRetryClick = {},
            onExportClick = {},
            onOpenReport = {}
        )
    }
}
