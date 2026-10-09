package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt



//here we hold all the states the screen we are working on well need, like loading, error popup, some other domain
//related stuff aswell
data class LiveVitalsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val careRecipientFirstName: String = "",
    val vitals: LiveVitalSigns? = null,
    val hasWristband: Boolean = false,
    // Server time of the last refresh; "Hace 2 min" is measured against it, never the phone clock
    val now: Instant = Instant.EPOCH,
    // Today's readings on the phone's calendar, oldest first: the history of the day plus every live refresh since
    val todayReadings: List<VitalSignReading> = emptyList(),
    val zone: ZoneId = ZoneId.systemDefault()
) {
    val allWithinRange: Boolean get() = vitals?.allWithinRange ?: false

    val hasLiveSignal: Boolean get() = vitals?.hasLiveSignal ?: false

    // The backend stores systolic and diastolic as two readings; the screen shows them as one
    val bloodPressureText: String?
        get() {
            val systolic = vitals?.get(VitalSignType.BP_SYS) ?: return null
            val diastolic = vitals[VitalSignType.BP_DIA] ?: return null
            return "${systolic.value.roundToInt()}/${diastolic.value.roundToInt()}"
        }

    val bloodPressureOutOfRange: Boolean
        get() = vitals?.get(VitalSignType.BP_SYS)?.classification?.isOutRange == true ||
            vitals?.get(VitalSignType.BP_DIA)?.classification?.isOutRange == true

    fun todayValues(type: VitalSignType): List<Double> = todayReadings.filter { it.type == type }.map { it.value }

    /** Latest reading against the few before it, measured in tenths of its normal range so every sign weighs alike. */
    fun trend(type: VitalSignType): VitalTrend? {
        val latest = vitals?.get(type) ?: return null
        val earlier = todayReadings
            .filter { it.type == type && it.measuredAt < latest.measuredAt }
            .takeLast(TREND_WINDOW)
            .ifEmpty { return null }
        val change = latest.value - earlier.map { it.value }.average()
        val tolerance = (latest.normalMaximum - latest.normalMinimum) * TREND_TOLERANCE
        return when {
            change > tolerance -> VitalTrend.RISING
            change < -tolerance -> VitalTrend.FALLING
            else -> VitalTrend.STABLE
        }
    }

    // A live reading may also come back in the history, so they are told apart by type and time, not by id
    fun withReadings(added: List<VitalSignReading>): LiveVitalsUiState {
        val today = now.atZone(zone).toLocalDate()
        return copy(
            todayReadings = (todayReadings + added)
                .distinctBy { it.type to it.measuredAt }
                .filter { it.measuredAt.atZone(zone).toLocalDate() == today }
                .sortedBy { it.measuredAt }
        )
    }

    private companion object {
        const val TREND_WINDOW = 6
        const val TREND_TOLERANCE = 0.1
    }
}

enum class VitalTrend {
    STABLE,
    RISING,
    FALLING
}
