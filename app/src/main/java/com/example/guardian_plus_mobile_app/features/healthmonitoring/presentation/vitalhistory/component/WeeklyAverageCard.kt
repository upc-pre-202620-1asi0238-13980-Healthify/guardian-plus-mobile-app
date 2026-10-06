package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.displayUnit
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.format
import java.time.DayOfWeek
import java.time.LocalDate

/** "PROMEDIO SEMANAL 76.3 lpm · Mín 72 · Máx 80" and the chart of the selected vital sign. */
@Composable
fun WeeklyAverageCard(
    modifier: Modifier = Modifier,
    type: VitalSignType,
    average: Double?,
    min: Double?,
    max: Double?,
    dailyAverages: List<Double?>,
    days: List<LocalDate>
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.health_weekly_average),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (average != null) {
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 4.dp)) {
                            // One decimal even for whole-number signs: 76.3 lpm says more than 76 for a week
                            Text(
                                text = "%.1f".format(average),
                                style = MaterialTheme.typography.dataMetric.copy(fontSize = 36.sp, lineHeight = 40.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = " " + type.displayUnit,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.health_no_readings),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    MinMax(label = stringResource(R.string.health_min), value = min?.let { type.format(it) })
                    VerticalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                    MinMax(label = stringResource(R.string.health_max), value = max?.let { type.format(it) })
                }
            }
            WeeklyLineChart(modifier = Modifier.padding(top = 20.dp), values = dailyAverages)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { day ->
                    Text(
                        text = day.dayOfWeek.letter(),
                        style = MaterialTheme.typography.dataLabel,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MinMax(label: String, value: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.dataLabel, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value ?: "--",
            style = MaterialTheme.typography.dataLabel.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Spanish weekday initials; Wednesday is "X" so it is not confused with Tuesday's "M"
private fun DayOfWeek.letter(): String = when (this) {
    DayOfWeek.MONDAY -> "L"
    DayOfWeek.TUESDAY -> "M"
    DayOfWeek.WEDNESDAY -> "X"
    DayOfWeek.THURSDAY -> "J"
    DayOfWeek.FRIDAY -> "V"
    DayOfWeek.SATURDAY -> "S"
    DayOfWeek.SUNDAY -> "D"
}

@Preview
@Composable
private fun WeeklyAverageCardPreview() {
    val today = LocalDate.parse("2026-10-06")
    GuardianTheme(dynamicColor = false) {
        WeeklyAverageCard(
            type = VitalSignType.HR,
            average = 76.3,
            min = 72.0,
            max = 80.0,
            dailyAverages = listOf(75.0, 77.0, 73.0, 80.0, 76.0, 77.0, 77.0),
            days = (6 downTo 0).map { today.minusDays(it.toLong()) }
        )
    }
}
