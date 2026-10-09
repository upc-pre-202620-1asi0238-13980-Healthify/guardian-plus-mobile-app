package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.time.LocalDate

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

    LaunchedEffect(reportId) {
        viewModel.load(reportId)
    }

    HealthReportContent(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onRetryClick = { viewModel.load(reportId) },
        onOpenReport = onOpenReport
    )
}

@Composable
fun HealthReportContent(
    modifier: Modifier = Modifier,
    uiState: HealthReportUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
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
            onOpenReport = {}
        )
    }
}
