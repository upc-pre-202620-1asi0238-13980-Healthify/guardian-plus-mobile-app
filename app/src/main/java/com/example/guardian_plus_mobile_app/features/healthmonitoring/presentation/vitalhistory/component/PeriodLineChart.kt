package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

/** Line of the history card, one point per hour or per day. Empty points are skipped, the line joins the ones around them. */
@Composable
fun PeriodLineChart(modifier: Modifier = Modifier, values: List<Double?>) {
    val lineColor = MaterialTheme.colorScheme.primary
    val fillColor = MaterialTheme.colorScheme.primaryContainer
    val dotFill = MaterialTheme.colorScheme.surface

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
    ) {
        val known = values.filterNotNull()
        if (known.isEmpty() || values.size < 2) return@Canvas

        // The same inset on both axes keeps the first and last dots inside the canvas instead of cut in half
        val inset = 6.dp.toPx()
        val min = known.min()
        val max = known.max()
        val usableHeight = size.height - 2 * inset
        val stepX = (size.width - 2 * inset) / (values.size - 1)

        val points = values.mapIndexedNotNull { index, value ->
            value?.let {
                // A flat series would divide by zero; draw it as a line in the middle instead
                val normalized = if (max == min) 0.5 else (it - min) / (max - min)
                // Canvas y grows downwards, so higher values go nearer the top
                Offset(inset + index * stepX, inset + usableHeight * (1 - normalized).toFloat())
            }
        }

        if (points.size == 1) {
            // A single point has no line to draw yet; a dashed level keeps the card from looking empty
            val y = points.first().y
            drawLine(
                color = lineColor.copy(alpha = 0.4f),
                start = Offset(inset, y),
                end = Offset(size.width - inset, y),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()))
            )
        } else {
            val line = Path().apply {
                points.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
            }
            val area = Path().apply {
                addPath(line)
                lineTo(points.last().x, size.height)
                lineTo(points.first().x, size.height)
                close()
            }
            drawPath(area, Brush.verticalGradient(listOf(fillColor, Color.Transparent)))
            drawPath(line, lineColor, style = Stroke(width = 2.dp.toPx()))
        }

        // Small squares, as in the prototype. Long periods only mark the latest one, or the dots become a band
        val dot = 6.dp.toPx()
        val marked = if (values.size <= MAX_MARKED_POINTS) points else listOf(points.last())
        marked.forEach { p ->
            val topLeft = Offset(p.x - dot / 2, p.y - dot / 2)
            drawRect(dotFill, topLeft = topLeft, size = Size(dot, dot))
            drawRect(lineColor, topLeft = topLeft, size = Size(dot, dot), style = Stroke(width = 1.5.dp.toPx()))
        }
    }
}

private const val MAX_MARKED_POINTS = 10

@Preview(showBackground = true)
@Composable
private fun PeriodLineChartPreview() {
    GuardianTheme(dynamicColor = false) {
        PeriodLineChart(
            modifier = Modifier.padding(16.dp),
            values = listOf(75.0, 77.0, 73.0, 80.0, null, 77.0, 77.0)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PeriodLineChartSingleDayPreview() {
    GuardianTheme(dynamicColor = false) {
        PeriodLineChart(
            modifier = Modifier.padding(16.dp),
            values = listOf(null, null, null, null, null, null, 77.0)
        )
    }
}
