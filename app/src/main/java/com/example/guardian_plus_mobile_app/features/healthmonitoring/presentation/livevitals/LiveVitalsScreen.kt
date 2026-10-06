package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.relativeTime
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSign
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.ErrorState
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.LiveVitalsUiState
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.displayUnit
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.format
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.previewLiveVitals
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.previewNow
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.component.VitalTile

// Blood pressure is one tile for both halves, so BP_DIA is not listed
private val tileTypes = listOf(
    VitalSignType.HR,
    VitalSignType.BP_SYS,
    VitalSignType.SPO2,
    VitalSignType.TEMP,
    VitalSignType.RESP_RATE
)

/** "Salud › Ahora": the latest reading of every vital sign. */
@Composable
fun LiveVitalsScreen(
    modifier: Modifier = Modifier,
    viewModel: LiveVitalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LiveVitalsContent(modifier = modifier, uiState = uiState, onRetryClick = viewModel::load)
}

@Composable
fun LiveVitalsContent(
    modifier: Modifier = Modifier,
    uiState: LiveVitalsUiState,
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

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tileTypes, key = { it.name }) { type ->
                if (type == VitalSignType.BP_SYS) {
                    val systolic = vitals[VitalSignType.BP_SYS]
                    val diastolic = vitals[VitalSignType.BP_DIA]
                    VitalTile(
                        type = type,
                        valueText = uiState.bloodPressureText,
                        rangeText = if (systolic != null && diastolic != null) {
                            stringResource(
                                R.string.health_normal_range,
                                "${type.format(systolic.normalMinimum)}/${type.format(diastolic.normalMinimum)}",
                                "${type.format(systolic.normalMaximum)}/${type.format(diastolic.normalMaximum)}",
                                type.displayUnit
                            )
                        } else {
                            null
                        },
                        withinRange = !uiState.bloodPressureOutOfRange,
                        timeText = systolic?.let { relativeTime(it.measuredAt, uiState.now) }
                    )
                } else {
                    val reading = vitals[type]
                    VitalTile(
                        type = type,
                        valueText = reading?.let { type.format(it.value) },
                        rangeText = reading?.let { rangeText(it) },
                        withinRange = reading?.classification?.isOutRange != true,
                        timeText = reading?.let { relativeTime(it.measuredAt, uiState.now) }
                    )
                }
            }
        }
    }
}

@Composable
private fun rangeText(reading: LiveVitalSign): String = stringResource(
    R.string.health_normal_range,
    reading.type.format(reading.normalMinimum),
    reading.type.format(reading.normalMaximum),
    reading.type.displayUnit
)

@Preview(showBackground = true, heightDp = 760)
@Composable
private fun LiveVitalsContentPreview() {
    GuardianTheme(dynamicColor = false) {
        LiveVitalsContent(
            uiState = LiveVitalsUiState(vitals = previewLiveVitals(heartRate = 112.0), now = previewNow),
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
