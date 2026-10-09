package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.relativeTime
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.ErrorState
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.LiveVitalsUiState
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.VitalFilter
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.VitalFilterOption
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.displayUnit
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.displayedVitalTypes
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.format
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.labelRes
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.previewLiveVitals
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.previewNow
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.title
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.component.HeartRateHeroCard
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.component.RangeMarker
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.component.SyncStatusCard
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.component.VitalTile

// Enough to show the shape of the last minutes without crowding the card
private const val SPARKLINE_POINTS = 40

/** "Salud › Ahora": the heart rate up front, then the latest reading of every other vital sign. */
@Composable
fun LiveVitalsScreen(
    modifier: Modifier = Modifier,
    viewModel: LiveVitalsViewModel = hiltViewModel(),
    filter: VitalFilter = VitalFilter()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LiveVitalsContent(modifier = modifier, uiState = uiState, filter = filter, onRetryClick = viewModel::load)
}

@Composable
fun LiveVitalsContent(
    modifier: Modifier = Modifier,
    uiState: LiveVitalsUiState,
    filter: VitalFilter = VitalFilter(),
    onRetryClick: () -> Unit
) {
    val vitals = uiState.vitals
    when {
        vitals == null && uiState.isLoading -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        vitals == null || vitals.vitalSigns.isEmpty() -> ErrorState(
            modifier = modifier,
            message = uiState.errorMessage ?: stringResource(R.string.health_no_live_readings),
            onRetryClick = onRetryClick
        )

        else -> {
            val shown = displayedVitalTypes.filter { type ->
                filter.includes(type) && vitals[type] != null && filter.includesState(uiState.isWithinRange(type))
            }
            val showHero = VitalSignType.HR in shown
            val tiles = shown - VitalSignType.HR

            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showHero) {
                    item(key = "hero") { HeroCard(uiState) }
                }
                // Two per row as in the prototype; equal heights so the range bars line up
                items(tiles.chunked(2), key = { row -> row.joinToString { it.name } }) { row ->
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { type ->
                            Tile(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                uiState = uiState,
                                type = type
                            )
                        }
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
                if (shown.isEmpty()) {
                    item(key = "no-match") {
                        Text(
                            text = stringResource(R.string.health_no_filter_match),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        )
                    }
                }
                item(key = "sync") { SyncStatusCard(synced = uiState.errorMessage == null) }
            }
        }
    }
}

@Composable
private fun HeroCard(uiState: LiveVitalsUiState) {
    val reading = uiState.vitals?.get(VitalSignType.HR) ?: return
    val type = VitalSignType.HR
    val lastReading = relativeTime(reading.measuredAt, uiState.now).lowercaseFirst()
    val today = uiState.todayValues(type)

    HeartRateHeroCard(
        title = type.title,
        valueText = type.format(reading.value),
        unit = type.displayUnit,
        withinRange = uiState.isWithinRange(type),
        timeText = formatClockTime(reading.measuredAt),
        caption = uiState.trend(type)
            ?.let { stringResource(R.string.health_hero_caption, stringResource(it.labelRes()), lastReading) }
            ?: stringResource(R.string.health_last_reading, lastReading),
        sparkline = today.takeLast(SPARKLINE_POINTS).ifEmpty { listOf(reading.value) },
        wristbandText = stringResource(
            when {
                !uiState.hasWristband -> R.string.health_wristband_missing
                uiState.hasLiveSignal -> R.string.health_wristband_connected
                else -> R.string.health_wristband_no_signal
            }
        ),
        maxMinText = today.takeIf { it.isNotEmpty() }?.let {
            stringResource(R.string.health_max_min, type.format(it.max()), type.format(it.min()))
        }
    )
}

@Composable
private fun Tile(modifier: Modifier, uiState: LiveVitalsUiState, type: VitalSignType) {
    val reading = uiState.vitals?.get(type)
    val isBloodPressure = type == VitalSignType.BP_SYS
    val trend = uiState.trend(type)?.let { stringResource(it.labelRes()) }
    val time = reading?.let { relativeTime(it.measuredAt, uiState.now).lowercaseFirst() }

    VitalTile(
        modifier = modifier,
        title = type.title,
        valueText = if (isBloodPressure) uiState.bloodPressureText else reading?.let { type.format(it.value) },
        unit = type.displayUnit,
        withinRange = uiState.isWithinRange(type),
        // Blood pressure is placed by its systolic half, the one the range bar can show
        marker = reading?.let { RangeMarker(it.value, it.normalMinimum, it.normalMaximum) },
        caption = listOfNotNull(trend, time).joinToString(" · ").ifEmpty { null }
    )
}

private fun LiveVitalsUiState.isWithinRange(type: VitalSignType): Boolean =
    if (type == VitalSignType.BP_SYS) !bloodPressureOutOfRange else vitals?.get(type)?.classification?.isOutRange != true

// "Hace 3 min" reads "hace 3 min" in the middle of a sentence
private fun String.lowercaseFirst(): String = replaceFirstChar { it.lowercase() }

private fun previewToday(): List<VitalSignReading> =
    listOf(74.0, 75.0, 73.0, 76.0, 77.0, 79.0, 84.0, 77.0, 76.0, 71.0, 76.0, 75.0, 77.0).mapIndexed { index, value ->
        VitalSignReading("hr$index", VitalSignType.HR, value, previewNow.minusSeconds(900L - index * 60))
    }

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun LiveVitalsContentPreview() {
    GuardianTheme(dynamicColor = false) {
        LiveVitalsContent(
            uiState = LiveVitalsUiState(
                vitals = previewLiveVitals(),
                now = previewNow,
                hasWristband = true,
                todayReadings = previewToday()
            ),
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
private fun LiveVitalsContentFilteredPreview() {
    GuardianTheme(dynamicColor = false) {
        LiveVitalsContent(
            uiState = LiveVitalsUiState(vitals = previewLiveVitals(heartRate = 112.0), now = previewNow, hasWristband = true),
            filter = VitalFilter().toggle(VitalFilterOption.OBSERVATION),
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LiveVitalsContentErrorPreview() {
    GuardianTheme(dynamicColor = false) {
        LiveVitalsContent(
            uiState = LiveVitalsUiState(errorMessage = "No se pudo conectar con el servidor. Revisa tu conexión."),
            onRetryClick = {}
        )
    }
}
