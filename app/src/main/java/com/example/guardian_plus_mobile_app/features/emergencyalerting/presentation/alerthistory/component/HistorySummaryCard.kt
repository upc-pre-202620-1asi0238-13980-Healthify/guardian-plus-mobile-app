package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory.HistorySummary
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatDuration

private const val COMPACT_LIMIT_SEC = 600L

/** Week at a glance: how many alerts, how fast the Care Circle answered and how many escalated. */
@Composable
fun HistorySummaryCard(modifier: Modifier = Modifier, summary: HistorySummary) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {
            SummaryFigure(
                modifier = Modifier.weight(1f),
                value = summary.totalAlerts.toString(),
                label = stringResource(R.string.history_summary_total),
                color = MaterialTheme.colorScheme.onSurface
            )
            SummaryFigure(
                modifier = Modifier.weight(1f),
                value = summary.averageResponseSec?.let { seconds ->
                    // Seconds read best in this narrow column ("81 s"); long waits fall back to minutes
                    if (seconds < COMPACT_LIMIT_SEC) stringResource(R.string.duration_seconds, seconds.toInt()) else formatDuration(seconds)
                } ?: stringResource(R.string.history_summary_no_value),
                label = stringResource(R.string.history_summary_response),
                color = MaterialTheme.colorScheme.primary
            )
            SummaryFigure(
                modifier = Modifier.weight(1f),
                value = summary.escalatedCount.toString(),
                label = stringResource(R.string.history_summary_escalated),
                color = if (summary.escalatedCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SummaryFigure(modifier: Modifier = Modifier, value: String, label: String, color: Color) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.dataMetric, color = color, maxLines = 1)
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistorySummaryCardPreview() {
    GuardianTheme(dynamicColor = false) {
        HistorySummaryCard(
            modifier = Modifier.padding(16.dp),
            summary = HistorySummary(totalAlerts = 12, averageResponseSec = 48, escalatedCount = 2)
        )
    }
}
