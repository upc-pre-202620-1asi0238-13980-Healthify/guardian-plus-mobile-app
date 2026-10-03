package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// The interface is in Spanish, so dates are too, whatever the phone's language
private val dayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE d MMM", Locale.forLanguageTag("es"))

/** Section title of the history: "HOY", "AYER" or the date ("SÁBADO 26 SEPT."). */
@Composable
fun DayHeader(modifier: Modifier = Modifier, day: LocalDate, today: LocalDate) {
    val label = when (day) {
        today -> stringResource(R.string.history_today)
        today.minusDays(1) -> stringResource(R.string.history_yesterday)
        else -> dayFormatter.format(day)
    }
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun DayHeaderPreview() {
    GuardianTheme(dynamicColor = false) {
        DayHeader(day = LocalDate.of(2026, 9, 26), today = LocalDate.of(2026, 10, 3))
    }
}
