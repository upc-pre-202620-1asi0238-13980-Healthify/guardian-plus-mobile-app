package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.StatusChip
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.generatedAtText
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.labelRes
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.periodText
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.previewHealthReport

/** Green card that opens a report: its period, the totals of the period and when it was compiled. */
@Composable
fun ReportOverviewCard(modifier: Modifier = Modifier, report: HealthReport) {
    val onCard = MaterialTheme.colorScheme.onPrimary

    Surface(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.primary) {
        Column {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.report_period).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = onCard.copy(alpha = 0.8f)
                        )
                        Text(
                            text = report.periodText(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = onCard,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    StatusChip(
                        text = stringResource(report.reportType.labelRes),
                        container = MaterialTheme.colorScheme.primaryContainer,
                        content = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = stringResource(R.string.report_generated_at, report.generatedAtText()),
                    style = MaterialTheme.typography.bodySmall,
                    color = onCard.copy(alpha = 0.75f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            HorizontalDivider(color = onCard.copy(alpha = 0.15f))
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Total(modifier = Modifier.weight(1f), value = report.readingsCount, label = stringResource(R.string.report_readings))
                Total(modifier = Modifier.weight(1f), value = report.outOfRangeCount, label = stringResource(R.string.report_out_of_range))
                Total(
                    modifier = Modifier.weight(1f),
                    value = report.recurrentAnomaliesCount,
                    label = stringResource(R.string.report_recurrent_anomalies)
                )
            }
        }
    }
}

@Composable
private fun Total(modifier: Modifier, value: Int, label: String) {
    val onCard = MaterialTheme.colorScheme.onPrimary
    Column(modifier = modifier) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.dataMetric.copy(fontSize = 22.sp, lineHeight = 26.sp),
            color = onCard
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = onCard.copy(alpha = 0.75f))
    }
}

@Preview
@Composable
private fun ReportOverviewCardPreview() {
    GuardianTheme(dynamicColor = false) {
        ReportOverviewCard(report = previewHealthReport())
    }
}
