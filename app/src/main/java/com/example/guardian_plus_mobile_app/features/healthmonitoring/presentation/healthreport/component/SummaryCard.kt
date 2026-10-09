package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.StatusChip
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.StabilityIndex
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.displayUnit
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.title
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.SummaryRow
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.labelRes
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.previewHealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.summaryRows

/** One vital sign of a report: its average, extremes, how many readings fell outside the range and its stability. */
@Composable
fun SummaryCard(modifier: Modifier = Modifier, row: SummaryRow) {
    val (container, content) = row.stability.colors()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = row.type.title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(text = stringResource(row.stability.labelRes), container = container, content = content)
            }
            Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.Bottom) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.report_average),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = row.averageText,
                            style = MaterialTheme.typography.dataMetric.copy(fontSize = 26.sp, lineHeight = 30.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = " " + row.type.displayUnit,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.health_max_min, row.maxText, row.minText),
                    style = MaterialTheme.typography.dataLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Text(
                text = pluralStringResource(R.plurals.report_readings_count, row.readingsCount, row.readingsCount) + " · " +
                    pluralStringResource(R.plurals.report_out_of_range_count, row.outOfRangeCount, row.outOfRangeCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun StabilityIndex.colors(): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when (this) {
        StabilityIndex.STABLE -> scheme.primaryContainer to scheme.onPrimaryContainer
        StabilityIndex.UNSTABLE -> scheme.noticeContainer to scheme.onNoticeContainer
        StabilityIndex.RECURRENT -> scheme.errorContainer to scheme.onErrorContainer
    }
}

@Preview
@Composable
private fun SummaryCardPreview() {
    GuardianTheme(dynamicColor = false) {
        SummaryCard(row = previewHealthReport().summaryRows()[1])
    }
}
