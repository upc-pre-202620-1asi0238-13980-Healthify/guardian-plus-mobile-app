package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric

/** Heart rate and SpO2 at the moment of the alert (Health Monitoring, simulated for now). */
@Composable
fun VitalsRow(
    modifier: Modifier = Modifier,
    heartRateBpm: Int?,
    oxygenSaturation: Int?
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        heartRateBpm?.let { bpm ->
            VitalTile(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.detail_heart_rate),
                value = bpm.toString(),
                unit = stringResource(R.string.unit_bpm),
                reading = when {
                    bpm > HEART_RATE_HIGH -> Reading.HIGH
                    bpm < HEART_RATE_LOW -> Reading.LOW
                    else -> Reading.NORMAL
                }
            )
        }
        oxygenSaturation?.let { spo2 ->
            VitalTile(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.detail_oxygen),
                value = spo2.toString(),
                unit = stringResource(R.string.unit_percent),
                reading = if (spo2 < OXYGEN_LOW) Reading.LOW else Reading.NORMAL
            )
        }
    }
}

private enum class Reading { NORMAL, HIGH, LOW }

// Same ranges as the user stories of Health Monitoring (US01, US03)
private const val HEART_RATE_HIGH = 100
private const val HEART_RATE_LOW = 60
private const val OXYGEN_LOW = 92

@Composable
private fun VitalTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String,
    reading: Reading
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, style = MaterialTheme.typography.dataMetric, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            val (text, container, content) = when (reading) {
                Reading.HIGH -> Triple(
                    stringResource(R.string.reading_high),
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.tertiary
                )
                Reading.LOW -> Triple(
                    stringResource(R.string.reading_low),
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.tertiary
                )
                Reading.NORMAL -> Triple(
                    stringResource(R.string.reading_normal),
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            StatusChip(text = text, container = container, content = content)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VitalsRowPreview() {
    GuardianTheme(dynamicColor = false) {
        VitalsRow(modifier = Modifier.padding(16.dp), heartRateBpm = 112, oxygenSaturation = 95)
    }
}
