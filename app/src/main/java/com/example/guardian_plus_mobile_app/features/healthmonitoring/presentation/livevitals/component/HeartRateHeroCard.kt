package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.ReadingBadge

/** Green card that leads "Ahora": the heart rate, its line of the day and the wristband that measures it. */
@Composable
fun HeartRateHeroCard(
    modifier: Modifier = Modifier,
    title: String,
    valueText: String,
    unit: String,
    withinRange: Boolean,
    timeText: String,
    caption: String,
    sparkline: List<Double>,
    wristbandText: String,
    maxMinText: String?
) {
    val onCard = MaterialTheme.colorScheme.onPrimary

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primary
    ) {
        Column {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = onCard.copy(alpha = 0.8f)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = valueText,
                                style = MaterialTheme.typography.dataMetric.copy(fontSize = 56.sp, lineHeight = 64.sp),
                                color = onCard
                            )
                            Text(
                                text = " $unit",
                                style = MaterialTheme.typography.bodyLarge,
                                color = onCard.copy(alpha = 0.75f),
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        ReadingBadge(withinRange = withinRange)
                        Text(
                            text = timeText,
                            style = MaterialTheme.typography.dataLabel,
                            color = onCard.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
                Text(text = caption, style = MaterialTheme.typography.bodySmall, color = onCard.copy(alpha = 0.75f))
            }
            Sparkline(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                values = sparkline,
                lineColor = onCard.copy(alpha = 0.7f),
                fillColor = onCard.copy(alpha = 0.18f)
            )
            HorizontalDivider(color = onCard.copy(alpha = 0.15f))
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_watch),
                    contentDescription = null,
                    tint = onCard.copy(alpha = 0.75f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = wristbandText,
                    style = MaterialTheme.typography.bodySmall,
                    color = onCard.copy(alpha = 0.75f),
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                maxMinText?.let {
                    Text(text = it, style = MaterialTheme.typography.dataLabel, color = onCard.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Preview
@Composable
private fun HeartRateHeroCardPreview() {
    GuardianTheme(dynamicColor = false) {
        HeartRateHeroCard(
            title = "Frecuencia cardíaca",
            valueText = "78",
            unit = "lpm",
            withinRange = true,
            timeText = "14:32",
            caption = "Estable · última lectura hace 3 min",
            sparkline = listOf(74.0, 75.0, 73.0, 76.0, 77.0, 79.0, 77.0, 76.0, 78.0, 76.0, 75.0, 77.0, 78.0),
            wristbandText = "Pulsera conectada",
            maxMinText = "Máx 84 · Mín 71"
        )
    }
}
