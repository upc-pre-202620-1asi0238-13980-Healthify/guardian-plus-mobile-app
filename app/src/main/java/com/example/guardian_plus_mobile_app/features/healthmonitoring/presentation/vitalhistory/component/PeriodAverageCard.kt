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
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.HistoryPeriod
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.displayUnit
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.format
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.title
import java.time.DayOfWeek
import java.time.LocalDate

/** "FRECUENCIA CARDÍACA · PROMEDIO SEMANAL 76.3 lpm · Mín 72 · Máx 80" and the chart of one vital sign. */
@Composable
fun PeriodAverageCard(
    modifier: Modifier = Modifier,
    type: VitalSignType,
    period: HistoryPeriod,
    average: Double?,
    min: Double?,
    max: Double?,
    values: List<Double?>,
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
                        text = stringResource(R.string.health_average_title, type.title, stringResource(period.averageRes())).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (average != null) {
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 4.dp)) {
                            // One decimal even for whole-number signs: 76.3 lpm says more than 76 over a period
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
                            text = stringResource(period.emptyRes()),
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
            PeriodLineChart(modifier = Modifier.padding(top = 20.dp), values = values)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                axisLabels(period, days).forEach { label ->
                    Text(
                        text = label,
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

// Every day for a week; a few evenly spread marks for a day (hours) or a month (dates), or they would not fit
private fun axisLabels(period: HistoryPeriod, days: List<LocalDate>): List<String> = when (period) {
    HistoryPeriod.DAY -> listOf(0, 6, 12, 18, 23).map { "${it}h" }
    HistoryPeriod.WEEK -> days.map { it.dayOfWeek.letter() }
    HistoryPeriod.MONTH -> listOf(0, 7, 14, 22, days.lastIndex).map { days[it].dayOfMonth.toString() }
}

private fun HistoryPeriod.averageRes(): Int = when (this) {
    HistoryPeriod.DAY -> R.string.health_average_day
    HistoryPeriod.WEEK -> R.string.health_average_week
    HistoryPeriod.MONTH -> R.string.health_average_month
}

private fun HistoryPeriod.emptyRes(): Int = when (this) {
    HistoryPeriod.DAY -> R.string.health_no_readings_day
    HistoryPeriod.WEEK -> R.string.health_no_readings
    HistoryPeriod.MONTH -> R.string.health_no_readings_month
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
private fun PeriodAverageCardPreview() {
    val today = LocalDate.parse("2026-10-06")
    GuardianTheme(dynamicColor = false) {
        PeriodAverageCard(
            type = VitalSignType.HR,
            period = HistoryPeriod.WEEK,
            average = 76.3,
            min = 72.0,
            max = 80.0,
            values = listOf(75.0, 77.0, 73.0, 80.0, 76.0, 77.0, 77.0),
            days = (6 downTo 0).map { today.minusDays(it.toLong()) }
        )
    }
}

@Preview
@Composable
private fun PeriodAverageCardDayPreview() {
    val today = LocalDate.parse("2026-10-06")
    GuardianTheme(dynamicColor = false) {
        PeriodAverageCard(
            type = VitalSignType.SPO2,
            period = HistoryPeriod.DAY,
            average = 96.4,
            min = 94.0,
            max = 98.0,
            values = List(24) { hour -> if (hour in 8..20) 94.0 + hour % 5 else null },
            days = listOf(today)
        )
    }
}
