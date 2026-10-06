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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

/** Seven-day line of the prototype. Days without readings are skipped, the line joins the ones around them. */
@Composable
fun WeeklyLineChart(modifier: Modifier = Modifier, values: List<Double?>) {
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

        val inset = 6.dp.toPx()
        val min = known.min()
        val max = known.max()
        val usableHeight = size.height - 2 * inset
        val stepX = size.width / (values.size - 1)

        val points = values.mapIndexedNotNull { index, value ->
            value?.let {
                // A flat week would divide by zero; draw it as a line in the middle instead
                val normalized = if (max == min) 0.5 else (it - min) / (max - min)
                // Canvas y grows downwards, so higher values go nearer the top
                Offset(index * stepX, inset + usableHeight * (1 - normalized).toFloat())
            }
        }

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
        // Small squares, as in the prototype
        val dot = 6.dp.toPx()
        points.forEach { p ->
            val topLeft = Offset(p.x - dot / 2, p.y - dot / 2)
            drawRect(dotFill, topLeft = topLeft, size = Size(dot, dot))
            drawRect(lineColor, topLeft = topLeft, size = Size(dot, dot), style = Stroke(width = 1.5.dp.toPx()))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeeklyLineChartPreview() {
    GuardianTheme(dynamicColor = false) {
        WeeklyLineChart(
            modifier = Modifier.padding(16.dp),
            values = listOf(75.0, 77.0, 73.0, 80.0, null, 77.0, 77.0)
        )
    }
}
