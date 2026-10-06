package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import java.time.LocalDate
import java.time.ZoneId

data class VitalHistoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Oldest first, as the platform sends them; may include a few hours outside the week (see the ViewModel)
    val readings: List<VitalSignReading> = emptyList(),
    // History readings carry no range, so it is taken from the live endpoint
    val ranges: Map<VitalSignType, ClosedFloatingPointRange<Double>> = emptyMap(),
    val selectedType: VitalSignType = VitalSignType.HR,
    val today: LocalDate = LocalDate.now(),
    val zone: ZoneId = ZoneId.systemDefault()
) {
    val days: List<LocalDate>
        get() = (6 downTo 0).map { today.minusDays(it.toLong()) }

    // Only what happened in the last seven days on the phone's calendar
    val weekReadings: List<VitalSignReading>
        get() {
            val firstDay = days.first()
            return readings.filter { it.dayIn(zone) in firstDay..today }
        }

    val selectedReadings: List<VitalSignReading>
        get() = weekReadings.filter { it.type == selectedType }

    // average() of an empty list is NaN, which would print "NaN lpm"
    val weeklyAverage: Double?
        get() = selectedReadings.takeIf { it.isNotEmpty() }?.map { it.value }?.average()

    val weeklyMin: Double? get() = selectedReadings.minOfOrNull { it.value }

    val weeklyMax: Double? get() = selectedReadings.maxOfOrNull { it.value }

    // One point per day of the chart; null where the wearable sent nothing that day
    val dailyAverages: List<Double?>
        get() {
            val byDay = selectedReadings.groupBy { it.dayIn(zone) }
            return days.map { day -> byDay[day]?.map { it.value }?.average() }
        }

    // Diastolic readings are shown inside their systolic row ("122/80"), never alone
    val recentReadings: List<VitalSignReading>
        get() = weekReadings.filter { it.type != VitalSignType.BP_DIA }.takeLast(RECENT_COUNT).reversed()

    fun diastolicFor(systolic: VitalSignReading): VitalSignReading? =
        readings.firstOrNull { it.type == VitalSignType.BP_DIA && it.measuredAt == systolic.measuredAt }

    // Without a known range there is nothing to warn about
    fun isWithinRange(reading: VitalSignReading): Boolean {
        val withinOwnRange = ranges[reading.type]?.contains(reading.value) ?: true
        // A blood pressure row also covers its diastolic half
        val diastolic = if (reading.type == VitalSignType.BP_SYS) diastolicFor(reading) else null
        return withinOwnRange && (diastolic == null || isWithinRange(diastolic))
    }

    private fun VitalSignReading.dayIn(zone: ZoneId): LocalDate = measuredAt.atZone(zone).toLocalDate()

    private companion object {
        const val RECENT_COUNT = 3
    }
}
