package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.relativeTime
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSign
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.ReadingBadge
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.displayUnit
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.format
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.iconRes
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.label
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.previewLiveVitals
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.previewNow
import java.time.Instant

// The live endpoint returns one value per type, not a series, so the bars next to the heart rate are decorative
private val decorativeBarHeights = listOf(14, 20, 12, 24, 18, 30, 16, 22, 26, 18)

/** Heart rate in large type and the other four readings in a 2×2 grid. */
@Composable
fun VitalsSummaryCard(
    modifier: Modifier = Modifier,
    vitals: LiveVitalSigns,
    bloodPressureText: String?,
    bloodPressureOutOfRange: Boolean,
    now: Instant
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            HeartRateBlock(heartRate = vitals[VitalSignType.HR], now = now)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            // Plain rows instead of a lazy grid: a lazy layout inside the screen's LazyColumn cannot be measured
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                VitalCell(
                    modifier = Modifier.weight(1f),
                    type = VitalSignType.BP_SYS,
                    valueText = bloodPressureText,
                    outOfRange = bloodPressureOutOfRange
                )
                VerticalDivider(color = MaterialTheme.colorScheme.outline)
                VitalCell(modifier = Modifier.weight(1f), reading = vitals[VitalSignType.SPO2], type = VitalSignType.SPO2)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                VitalCell(modifier = Modifier.weight(1f), reading = vitals[VitalSignType.TEMP], type = VitalSignType.TEMP)
                VerticalDivider(color = MaterialTheme.colorScheme.outline)
                VitalCell(
                    modifier = Modifier.weight(1f),
                    reading = vitals[VitalSignType.RESP_RATE],
                    type = VitalSignType.RESP_RATE
                )
            }
        }
    }
}

@Composable
private fun HeartRateBlock(heartRate: LiveVitalSign?, now: Instant) {
    val outOfRange = heartRate?.classification?.isOutRange == true
    Column(modifier = Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(VitalSignType.HR.iconRes()),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = VitalSignType.HR.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = heartRate?.let { VitalSignType.HR.format(it.value) } ?: "--",
                        style = MaterialTheme.typography.dataMetric.copy(fontSize = 36.sp, lineHeight = 40.sp),
                        color = if (outOfRange) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " " + VitalSignType.HR.displayUnit,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                decorativeBarHeights.forEach { height ->
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(height.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), MaterialTheme.shapes.extraSmall)
                    )
                }
            }
        }
        if (heartRate != null) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = MaterialTheme.colorScheme.outline)
            Row(verticalAlignment = Alignment.CenterVertically) {
                ReadingBadge(withinRange = !outOfRange, long = true)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = relativeTime(heartRate.measuredAt, now),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun VitalCell(modifier: Modifier = Modifier, reading: LiveVitalSign?, type: VitalSignType) {
    VitalCell(
        modifier = modifier,
        type = type,
        valueText = reading?.let { type.format(it.value) },
        outOfRange = reading?.classification?.isOutRange == true
    )
}

@Composable
private fun VitalCell(
    modifier: Modifier = Modifier,
    type: VitalSignType,
    valueText: String?,
    outOfRange: Boolean
) {
    Column(modifier = modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                painter = painterResource(type.iconRes()),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Text(type.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 6.dp)) {
            Text(
                text = valueText ?: "--",
                style = MaterialTheme.typography.dataMetric,
                color = if (outOfRange) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = " " + type.displayUnit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

@Preview
@Composable
private fun VitalsSummaryCardPreview() {
    GuardianTheme(dynamicColor = false) {
        VitalsSummaryCard(
            vitals = previewLiveVitals(),
            bloodPressureText = "118/76",
            bloodPressureOutOfRange = false,
            now = previewNow
        )
    }
}
