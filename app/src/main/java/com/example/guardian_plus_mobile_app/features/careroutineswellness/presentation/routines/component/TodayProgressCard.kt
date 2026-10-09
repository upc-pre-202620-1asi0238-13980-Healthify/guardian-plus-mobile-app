package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.RoutineCount

/** Green "HOY" card: how many of today's routines are done, as a ring, a bar and two counters. */
@Composable
fun TodayProgressCard(
    modifier: Modifier = Modifier,
    dateLabel: String,
    count: RoutineCount
) {
    val scheme = MaterialTheme.colorScheme
    val onContainer = scheme.onPrimary
    // Secondary text in the pastel mint of the prototype, readable on the green
    val softText = scheme.primaryContainer

    Surface(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, color = scheme.primary) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = stringResource(R.string.routines_today_label, dateLabel),
                style = MaterialTheme.typography.dataLabel,
                color = onContainer
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(
                            when {
                                count.total == 0 -> R.string.routines_today_empty
                                count.pending == 0 -> R.string.routines_today_done
                                else -> R.string.routines_today_in_progress
                            }
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        color = onContainer
                    )
                    if (count.total > 0) {
                        Text(
                            text = stringResource(R.string.routines_today_progress, count.completed, count.total),
                            style = MaterialTheme.typography.bodySmall,
                            color = softText
                        )
                    }
                }
                ProgressRing(
                    percentage = count.percentage,
                    track = onContainer.copy(alpha = 0.17f),
                    progress = softText,
                    textColor = onContainer
                )
            }
            ProgressBar(percentage = count.percentage, track = onContainer.copy(alpha = 0.2f), progress = softText)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Counter(
                    iconRes = R.drawable.ic_check,
                    text = pluralStringResource(R.plurals.routines_completed_count, count.completed, count.completed),
                    color = onContainer
                )
                // Only shown when it happens, so the usual day looks like the prototype
                if (count.missed > 0) {
                    Counter(
                        iconRes = R.drawable.ic_x,
                        text = pluralStringResource(R.plurals.routines_missed_count, count.missed, count.missed),
                        color = onContainer
                    )
                }
                Counter(
                    iconRes = R.drawable.ic_clock,
                    text = pluralStringResource(R.plurals.routines_pending_count, count.pending, count.pending),
                    color = onContainer
                )
            }
        }
    }
}

@Composable
private fun ProgressRing(percentage: Int, track: Color, progress: Color, textColor: Color) {
    Box(modifier = Modifier.size(58.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 5.dp.toPx()
            val inset = strokeWidth / 2
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            drawArc(
                color = track,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = progress,
                startAngle = -90f,
                sweepAngle = 360f * percentage / 100f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
        }
        Text(
            text = stringResource(R.string.routines_percent, percentage),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

@Composable
private fun ProgressBar(percentage: Int, track: Color, progress: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(track, CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(percentage / 100f)
                .fillMaxHeight()
                .background(progress, CircleShape)
        )
    }
}

@Composable
private fun Counter(@DrawableRes iconRes: Int, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(painter = painterResource(iconRes), contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = color)
    }
}

@Preview
@Composable
private fun TodayProgressCardPreview() {
    GuardianTheme(dynamicColor = false) {
        TodayProgressCard(dateLabel = "MARTES 23", count = RoutineCount(completed = 5, pending = 2, missed = 0))
    }
}

@Preview
@Composable
private fun TodayProgressCardMissedPreview() {
    GuardianTheme(dynamicColor = false) {
        TodayProgressCard(dateLabel = "MARTES 23", count = RoutineCount(completed = 4, pending = 2, missed = 1))
    }
}
