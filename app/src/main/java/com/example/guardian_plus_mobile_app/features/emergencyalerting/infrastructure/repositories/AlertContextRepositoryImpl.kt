package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContextRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetLiveVitalSignsUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSign
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Reads the context of an alert from Health Monitoring: the wristband's live readings and whether it
 * still has signal. Only alerts raised by the wristband (fall, SOS, vital sign anomaly) carry readings;
 * the location will come from Mobility & Geofencing.
 */
class AlertContextRepositoryImpl @Inject constructor(
    private val getLiveVitalSigns: GetLiveVitalSignsUseCase
) : AlertContextRepository {

    override suspend fun getAlertContext(alert: Alert): Result<AlertContext> {
        if (alert.sourceType !in WRISTBAND_SOURCES) return Result.success(AlertContext())
        return getLiveVitalSigns(alert.careRecipientProfileId).map { it.toAlertContext(alert.sourceType) }
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
        const val WRISTBAND_CONNECTED = "Pulsera conectada"
        const val WRISTBAND_NO_SIGNAL = "Pulsera sin señal"
    }
}
