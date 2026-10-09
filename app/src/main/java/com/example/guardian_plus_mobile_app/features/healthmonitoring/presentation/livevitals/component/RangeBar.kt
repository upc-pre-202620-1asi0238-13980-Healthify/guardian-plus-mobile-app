package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

/** A reading and the normal range it is judged against. */
data class RangeMarker(val value: Double, val normalMinimum: Double, val normalMaximum: Double)

/**
 * Track with the normal range shaded and a tick on the reading. The track spans half a range more on each
 * side, so a normal value lands in the middle and an abnormal one visibly outside the shade.
 */
@Composable
fun RangeBar(modifier: Modifier = Modifier, marker: RangeMarker, withinRange: Boolean) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val rangeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
    val tickColor = if (withinRange) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        val span = (marker.normalMaximum - marker.normalMinimum).coerceAtLeast(1.0)
        val low = marker.normalMinimum - span / 2
        val high = marker.normalMaximum + span / 2
        fun x(value: Double): Float = (((value - low) / (high - low)).coerceIn(0.0, 1.0) * size.width).toFloat()

        val barHeight = 4.dp.toPx()
        val barTop = (size.height - barHeight) / 2
        val radius = CornerRadius(barHeight / 2)
        drawRoundRect(trackColor, topLeft = Offset(0f, barTop), size = Size(size.width, barHeight), cornerRadius = radius)
        val rangeStart = x(marker.normalMinimum)
        drawRoundRect(
            rangeColor,
            topLeft = Offset(rangeStart, barTop),
            size = Size(x(marker.normalMaximum) - rangeStart, barHeight),
            cornerRadius = radius
        )
        val tickWidth = 3.dp.toPx()
        val tickX = x(marker.value).coerceIn(tickWidth / 2, size.width - tickWidth / 2)
        drawRoundRect(
            tickColor,
            topLeft = Offset(tickX - tickWidth / 2, 0f),
            size = Size(tickWidth, size.height),
            cornerRadius = CornerRadius(tickWidth / 2)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RangeBarPreview() {
    GuardianTheme(dynamicColor = false) {
        RangeBar(modifier = Modifier.padding(16.dp), marker = RangeMarker(118.0, 90.0, 140.0), withinRange = true)
    }
}
