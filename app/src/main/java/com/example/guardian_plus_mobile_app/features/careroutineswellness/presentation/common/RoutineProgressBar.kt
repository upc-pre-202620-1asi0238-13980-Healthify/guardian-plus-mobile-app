package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R

/** Thin rounded bar with an optional "Adherencia diaria · 67%" line above it. */
@Composable
fun RoutineProgressBar(
    modifier: Modifier = Modifier,
    percentage: Int,
    label: String? = null,
    showPercentage: Boolean = label != null,
    track: Color = MaterialTheme.colorScheme.surfaceVariant,
    progress: Color = MaterialTheme.colorScheme.secondary,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null || showPercentage) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 7.dp)) {
                Text(
                    text = label.orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
                if (showPercentage) {
                    Text(
                        text = stringResource(R.string.routines_percent, percentage),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(track, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage.coerceIn(0, 100) / 100f)
                    .fillMaxHeight()
                    .background(progress, CircleShape)
            )
        }
    }
}
