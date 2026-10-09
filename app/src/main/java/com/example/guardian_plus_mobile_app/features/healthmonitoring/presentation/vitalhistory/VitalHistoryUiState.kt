package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.HistoryPeriod
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.VitalFilter
import java.time.LocalDate
import java.time.ZoneId

data class VitalHistoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Oldest first, as the platform sends them; may include a few hours outside the period (see the ViewModel)
    val readings: List<VitalSignReading> = emptyList(),
    // History readings carry no range, so it is taken from the live endpoint
    val ranges: Map<VitalSignType, ClosedFloatingPointRange<Double>> = emptyMap(),
    val filter: VitalFilter = VitalFilter(),
    val selectedType: VitalSignType = VitalSignType.HR,
    // The period the readings were loaded for, which trails the filter's while a new one loads
    val period: HistoryPeriod = HistoryPeriod.WEEK,
    val today: LocalDate = LocalDate.now(),
    val zone: ZoneId = ZoneId.systemDefault(),
    // The report being compiled on the platform, so its button shows progress and is not pressed twice
    val busyAction: ReportAction? = null,
    // The weekly report sheet is open while this is set
    val weeklyReport: WeeklyReport? = null,
    // One-shot events for the screen: a report to turn into a PDF and a message to show
    val reportToExport: HealthReport? = null,
    val actionMessage: String? = null
) {
    val days: List<LocalDate>
        get() = (period.dayCount - 1 downTo 0).map { today.minusDays(it.toLong()) }

    // Only what happened in the period on the phone's calendar
    val periodReadings: List<VitalSignReading>
        get() {
            val firstDay = days.first()
            return readings.filter { it.dayIn(zone) in firstDay..today }
        }

    // The chips only offer what the filter keeps, so a chip left out hands the chart to the first one kept
    val chartType: VitalSignType get() = selectedType.takeIf { it in filter.types } ?: filter.types.first()

    fun readingsOf(type: VitalSignType): List<VitalSignReading> = periodReadings.filter { it.type == type }

    // average() of an empty list is NaN, which would print "NaN lpm"
    fun average(type: VitalSignType): Double? = readingsOf(type).takeIf { it.isNotEmpty() }?.map { it.value }?.average()

    fun min(type: VitalSignType): Double? = readingsOf(type).minOfOrNull { it.value }

    fun max(type: VitalSignType): Double? = readingsOf(type).maxOfOrNull { it.value }

    /** One point per hour for a day and per day otherwise; null where the wearable sent nothing. */
    fun chartValues(type: VitalSignType): List<Double?> {
        val ofType = readingsOf(type)
        return if (period == HistoryPeriod.DAY) {
            val byHour = ofType.groupBy { it.measuredAt.atZone(zone).hour }
            (0 until HOURS_PER_DAY).map { hour -> byHour[hour]?.map { it.value }?.average() }
        } else {
            val byDay = ofType.groupBy { it.dayIn(zone) }
            days.map { day -> byDay[day]?.map { it.value }?.average() }
        }
    }

    // Diastolic readings are shown inside their systolic row ("122/80"), never alone
    val recentReadings: List<VitalSignReading>
        get() = periodReadings
            .filter { it.type != VitalSignType.BP_DIA && filter.includes(it.type) && filter.includesState(isWithinRange(it)) }
            .takeLast(RECENT_COUNT)
            .reversed()

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
        const val HOURS_PER_DAY = 24
    }
}

enum class ReportAction {
    EXPORT_PDF,
    WEEKLY_REPORT
}
