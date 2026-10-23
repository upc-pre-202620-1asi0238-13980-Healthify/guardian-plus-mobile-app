package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContextRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.location.PlaceNameResolver
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.CurrentLocationDto
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.LocationTrackingService
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetLiveVitalSignsUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSign
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.Instant
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Reads the context of an alert from the bounded contexts that own it: the wristband's live readings and
 * signal from Health Monitoring, and the last known position from Mobility & Geofencing. Each part is shown
 * when it can be read; one that fails or does not exist yet is simply left out.
 */
class AlertContextRepositoryImpl @Inject constructor(
    private val getLiveVitalSigns: GetLiveVitalSignsUseCase,
    private val locationTrackingService: LocationTrackingService,
    private val placeNameResolver: PlaceNameResolver
) : AlertContextRepository {

    override suspend fun getAlertContext(alert: Alert): Result<AlertContext> = coroutineScope {
        val careRecipientProfileId = alert.careRecipientProfileId
        val readings = async {
            if (alert.sourceType in WRISTBAND_SOURCES) getLiveVitalSigns(careRecipientProfileId).getOrNull() else null
        }
        val location = async {
            if (alert.sourceType in LOCATED_SOURCES) currentLocation(careRecipientProfileId) else null
        }
        val context = readings.await()?.toAlertContext(alert.sourceType) ?: AlertContext()
        Result.success(
            location.await()?.let { (name, recordedAt) ->
                context.copy(locationName = name, locationUpdatedAt = recordedAt)
            } ?: context
        )
    }

    // Mobility answers 404 until the wristband has sent a first position
    private suspend fun currentLocation(careRecipientProfileId: String): Pair<String, Instant?>? {
        val dto = apiCall<CurrentLocationDto, CurrentLocationDto?>(
            request = { locationTrackingService.getCurrentLocation(careRecipientProfileId) },
            onNotFound = { null }
        ) { it }.getOrNull() ?: return null
        val latitude = dto.latitude ?: return null
        val longitude = dto.longitude ?: return null
        val name = placeNameResolver.nameOf(latitude, longitude)
            ?: String.format(Locale.US, "%.5f, %.5f", latitude, longitude)
        return name to dto.recordedAt?.let(Instant::parse)
    }

    private fun LiveVitalSigns.toAlertContext(sourceType: AlertSourceType): AlertContext {
        // A reading older than the live window is the last known value, not what is happening now
        val live = vitalSigns.filter { it.liveSignal }
        return AlertContext(
            readingSummary = if (sourceType == AlertSourceType.VITAL_SIGN_ANOMALY) outOfRangeSummary(live) else null,
            heartRateBpm = live.valueOf(VitalSignType.HR),
            oxygenSaturation = live.valueOf(VitalSignType.SPO2),
            deviceSummary = if (hasLiveSignal) WRISTBAND_CONNECTED else WRISTBAND_NO_SIGNAL
        )
    }

    // "124 lpm fuera de rango", or "124 lpm · 3 lecturas fuera de rango" when more than one is off
    private fun outOfRangeSummary(live: List<LiveVitalSign>): String? {
        val outOfRange = live.filter { it.classification.isOutRange }
        val first = outOfRange.firstOrNull() ?: return null
        val reading = "${first.type.format(first.value)} ${first.type.displayUnit}"
        return if (outOfRange.size == 1) "$reading fuera de rango"
        else "$reading · ${outOfRange.size} lecturas fuera de rango"
    }

    private fun List<LiveVitalSign>.valueOf(type: VitalSignType): Int? =
        firstOrNull { it.type == type }?.value?.roundToInt()

    private fun VitalSignType.format(value: Double): String =
        if (this == VitalSignType.TEMP) "%.1f".format(value) else value.roundToInt().toString()

    private val VitalSignType.displayUnit: String
        get() = when (this) {
            VitalSignType.HR -> "lpm"
            VitalSignType.BP_SYS, VitalSignType.BP_DIA -> "mmHg"
            VitalSignType.SPO2 -> "%"
            VitalSignType.TEMP -> "°C"
            VitalSignType.RESP_RATE -> "rpm"
        }

    private companion object {
        val WRISTBAND_SOURCES = setOf(
            AlertSourceType.FALL_DETECTED,
            AlertSourceType.SOS_TRIGGERED,
            AlertSourceType.VITAL_SIGN_ANOMALY
        )
        // Where the person is matters when someone has to go there
        val LOCATED_SOURCES = setOf(
            AlertSourceType.FALL_DETECTED,
            AlertSourceType.SOS_TRIGGERED,
            AlertSourceType.SAFE_ZONE_VIOLATION,
            AlertSourceType.PROLONGED_INACTIVITY
        )
        const val WRISTBAND_CONNECTED = "Pulsera conectada"
        const val WRISTBAND_NO_SIGNAL = "Pulsera sin señal"
    }
}
