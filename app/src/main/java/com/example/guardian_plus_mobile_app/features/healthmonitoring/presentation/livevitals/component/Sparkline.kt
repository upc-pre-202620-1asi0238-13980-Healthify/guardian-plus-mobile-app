package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Thin line of the latest readings, oldest on the left, ending in a dot on the current one. */
@Composable
fun Sparkline(
    modifier: Modifier = Modifier,
    values: List<Double>,
    lineColor: Color,
    fillColor: Color
) {
    Canvas(modifier = modifier) {
        if (values.isEmpty()) return@Canvas

        val inset = 4.dp.toPx()
        val min = values.min()
        val max = values.max()
        val usableHeight = size.height - 2 * inset
        fun y(value: Double): Float {
            // A flat series would divide by zero; it is drawn through the middle instead
            val normalized = if (max == min) 0.5 else (value - min) / (max - min)
            return inset + usableHeight * (1 - normalized).toFloat()
        }

        // One reading is still a level worth showing, so it spans the whole width
        val points = if (values.size == 1) {
            listOf(Offset(0f, y(values.first())), Offset(size.width - inset, y(values.first())))
        } else {
            val stepX = (size.width - inset) / (values.size - 1)
            values.mapIndexed { index, value -> Offset(index * stepX, y(value)) }
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
        drawPath(line, lineColor, style = Stroke(width = 1.5.dp.toPx()))
        drawCircle(lineColor, radius = 3.dp.toPx(), center = points.last())
    }
}

@Preview
@Composable
private fun SparklinePreview() {
    Sparkline(
        modifier = Modifier
            .background(Color(0xFF167A62))
            .padding(16.dp)
            .fillMaxWidth()
            .height(56.dp),
        values = listOf(74.0, 75.0, 73.0, 76.0, 77.0, 79.0, 77.0, 76.0, 78.0, 76.0, 75.0, 77.0, 78.0),
        lineColor = Color.White.copy(alpha = 0.7f),
        fillColor = Color.White.copy(alpha = 0.2f)
    )
}
