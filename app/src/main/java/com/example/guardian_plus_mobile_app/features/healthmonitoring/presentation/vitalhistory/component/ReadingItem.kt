package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.ReadingBadge
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.displayUnit
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.format
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.label
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dayMonth: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")

/** "Hoy · 14:32 · Ritmo cardíaco · 78 lpm · Normal": one past reading. */
@Composable
fun ReadingItem(
    modifier: Modifier = Modifier,
    reading: VitalSignReading,
    diastolic: VitalSignReading?,
    withinRange: Boolean,
    today: LocalDate,
    zone: ZoneId
) {
    val day = reading.measuredAt.atZone(zone).toLocalDate()
    val dayText = when (day) {
        today -> stringResource(R.string.health_today)
        today.minusDays(1) -> stringResource(R.string.health_yesterday)
        else -> dayMonth.format(day)
    }
    val type = reading.type
    val valueText = if (type == VitalSignType.BP_SYS && diastolic != null) {
        "${type.format(reading.value)}/${type.format(diastolic.value)} ${type.displayUnit}"
    } else {
        "${type.format(reading.value)} ${type.displayUnit}"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$dayText · ${formatClockTime(reading.measuredAt)}",
                    style = MaterialTheme.typography.dataLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = type.label,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Text(
                text = valueText,
                style = MaterialTheme.typography.dataLabel.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            ReadingBadge(withinRange = withinRange)
        }
    }
}

@Preview
@Composable
private fun ReadingItemPreview() {
    val at = Instant.parse("2026-10-05T03:04:00Z")
    GuardianTheme(dynamicColor = false) {
        ReadingItem(
            reading = VitalSignReading("1", VitalSignType.BP_SYS, 122.0, at),
            diastolic = VitalSignReading("2", VitalSignType.BP_DIA, 80.0, at),
            withinRange = false,
            today = LocalDate.parse("2026-10-06"),
            zone = ZoneId.of("America/Lima")
        )
    }
}
