package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.sleep

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataMetric
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onRestContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.restContainer
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepClassification
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecord
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineBadge
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineBanner
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineEmptyState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineErrorState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineInfoLine
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineLoadingState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineProgressBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSectionTitle
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTone
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.formatDayAndMonth
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.formatShortWeekday
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.formatSleepDuration
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

// Continuity from which a night reads as restful, matching the platform's "few interruptions"
private const val GOOD_CONTINUITY = 80

/** "Sueño": last night in detail and the nights of the past week. */
@Composable
fun SleepScreen(
    modifier: Modifier = Modifier,
    viewModel: SleepViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SleepContent(modifier = modifier, uiState = uiState, onBackClick = onBackClick, onRetryClick = viewModel::load)
}

@Composable
fun SleepContent(
    modifier: Modifier = Modifier,
    uiState: SleepUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    val lastNight = uiState.lastNight

    Column(modifier = modifier.fillMaxSize()) {
        RoutineTopBar(
            title = stringResource(R.string.routines_category_sleep),
            subtitle = stringResource(R.string.sleep_subtitle),
            onBackClick = onBackClick,
            action = lastNight?.let { night -> { ClassificationBadge(night.classification) } }
        )
        Box(modifier = Modifier.weight(1f)) {
            when {
                !uiState.isLoaded && uiState.errorMessage == null -> RoutineLoadingState()
                !uiState.isLoaded -> RoutineErrorState(message = uiState.errorMessage.orEmpty(), onRetryClick = onRetryClick)
                lastNight == null -> RoutineEmptyState(
                    iconRes = R.drawable.ic_moon,
                    title = stringResource(R.string.routines_sleep_none),
                    message = stringResource(R.string.sleep_empty_message)
                )

                else -> SleepList(uiState = uiState, lastNight = lastNight)
            }
        }
    }
}

@Composable
private fun SleepList(uiState: SleepUiState, lastNight: SleepCycleRecord) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "hero") { LastNightCard(night = lastNight, now = uiState.now) }
        item(key = "metrics") {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.sleep_continuity),
                    value = stringResource(R.string.routines_percent, lastNight.continuityScore),
                    detail = stringResource(
                        if (lastNight.continuityScore >= GOOD_CONTINUITY) R.string.sleep_continuity_good else R.string.sleep_continuity_interrupted
                    )
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.sleep_awakenings),
                    value = lastNight.interruptionCount.toString(),
                    detail = stringResource(R.string.sleep_awakenings_detail)
                )
            }
        }
        if (lastNight.classification == SleepClassification.FRAGMENTED) {
            item(key = "fragmented") {
                RoutineBanner(
                    iconRes = R.drawable.ic_moon,
                    title = stringResource(R.string.sleep_fragmented_title),
                    message = stringResource(R.string.sleep_fragmented_message),
                    tone = RoutineTone.ORANGE
                )
            }
        }
        item(key = "history-title") { RoutineSectionTitle(text = stringResource(R.string.routines_last_seven_days)) }
        item(key = "history") { SleepHistoryCard(nights = uiState.nights) }
        item(key = "note") { RoutineInfoLine(iconRes = R.drawable.ic_watch, text = stringResource(R.string.sleep_data_note)) }
    }
}

/** "ANOCHE · 22:48–06:36 · 7 h 24 min" on a violet moon. */
@Composable
private fun LastNightCard(night: SleepCycleRecord, now: Instant) {
    val zone = ZoneId.systemDefault()
    val endedToday = night.endTime.atZone(zone).toLocalDate() == now.atZone(zone).toLocalDate()
    val range = "${formatClockTime(night.startTime)}–${formatClockTime(night.endTime)}"

    RoutineCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.restContainer, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_moon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onRestContainer,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (endedToday) {
                        stringResource(R.string.sleep_last_night_label, range)
                    } else {
                        stringResource(R.string.sleep_night_of_label, formatDayAndMonth(night.endTime).uppercase(), range)
                    },
                    style = MaterialTheme.typography.dataLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatSleepDuration(night.durationMinutes),
                    style = MaterialTheme.typography.dataMetric,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.sleep_total_time),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MetricCard(modifier: Modifier = Modifier, label: String, value: String, detail: String) {
    RoutineCard(modifier = modifier) {
        Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.dataMetric, color = MaterialTheme.colorScheme.onSurface)
        Text(text = detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** One row per night: weekday, time asleep and continuity. */
@Composable
private fun SleepHistoryCard(nights: List<SleepCycleRecord>) {
    val zone = ZoneId.systemDefault()
    RoutineCard {
        nights.forEachIndexed { index, night ->
            if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatShortWeekday(night.endTime.atZone(zone).toLocalDate()),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(40.dp)
                )
                Text(
                    text = formatSleepDuration(night.durationMinutes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(84.dp)
                )
                RoutineProgressBar(
                    modifier = Modifier.weight(1f),
                    percentage = night.continuityScore,
                    progress = if (night.classification == SleepClassification.FRAGMENTED) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
            }
        }
    }
}

@Composable
private fun ClassificationBadge(classification: SleepClassification) {
    if (classification == SleepClassification.FRAGMENTED) {
        RoutineBadge(text = stringResource(R.string.routines_sleep_fragmented), tone = RoutineTone.ORANGE)
    } else {
        RoutineBadge(text = stringResource(R.string.routines_sleep_regular))
    }
}

private val previewNow: Instant = Instant.parse("2026-10-08T18:35:00Z")

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun SleepContentPreview() {
    val nights = listOf(444L to 76, 402L to 70, 470L to 86).mapIndexed { index, (minutes, score) ->
        val end = previewNow - Duration.ofHours(12) - Duration.ofDays(index.toLong())
        SleepCycleRecord(
            id = "n$index",
            startTime = end - Duration.ofMinutes(minutes),
            endTime = end,
            durationMinutes = minutes,
            interruptionCount = if (index == 0) 5 else 2,
            continuityScore = score,
            classification = if (index == 0) SleepClassification.FRAGMENTED else SleepClassification.REGULAR
        )
    }
    GuardianTheme(dynamicColor = false) {
        SleepContent(uiState = SleepUiState(isLoaded = true, nights = nights, now = previewNow), onBackClick = {}, onRetryClick = {})
    }
}
