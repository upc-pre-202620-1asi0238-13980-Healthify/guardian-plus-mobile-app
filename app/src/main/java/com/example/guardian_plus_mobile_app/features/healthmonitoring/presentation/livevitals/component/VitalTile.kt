package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.ReadingBadge

/** One cell of the "Ahora" grid: the latest value, where it sits in its normal range and how it is moving. */
@Composable
fun VitalTile(
    modifier: Modifier = Modifier,
    title: String,
    valueText: String?,
    unit: String,
    withinRange: Boolean,
    marker: RangeMarker?,
    caption: String?
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp)
                )
                if (valueText != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    ReadingBadge(withinRange = withinRange)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.padding(top = 12.dp), verticalAlignment = Alignment.Bottom) {
                Text(
                    text = valueText ?: "--",
                    style = MaterialTheme.typography.dataMetric.copy(fontSize = 26.sp, lineHeight = 30.sp),
                    color = if (withinRange) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = " $unit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            if (marker != null) {
                RangeBar(modifier = Modifier.padding(top = 12.dp), marker = marker, withinRange = withinRange)
            }
            caption?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun VitalTilePreview() {
    GuardianTheme(dynamicColor = false) {
        VitalTile(
            modifier = Modifier.width(180.dp),
            title = "Presión arterial",
            valueText = "118/76",
            unit = "mmHg",
            withinRange = true,
            marker = RangeMarker(118.0, 90.0, 140.0),
            caption = "Estable · hace 5 min"
        )
    }
}
