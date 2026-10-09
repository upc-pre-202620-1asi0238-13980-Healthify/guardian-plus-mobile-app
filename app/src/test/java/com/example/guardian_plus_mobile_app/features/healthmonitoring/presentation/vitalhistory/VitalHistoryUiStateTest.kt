package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.HistoryPeriod
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.VitalFilter
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.VitalFilterOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class VitalHistoryUiStateTest {

    private val lima = ZoneId.of("America/Lima")
    private val today = LocalDate.parse("2026-10-06")

    private fun reading(type: VitalSignType, value: Double, at: String) =
        VitalSignReading(id = "$type@$at", type = type, value = value, measuredAt = Instant.parse(at))

    private fun state(vararg readings: VitalSignReading) = filtered(VitalFilter(), HistoryPeriod.WEEK, *readings)

    private fun filtered(filter: VitalFilter, period: HistoryPeriod, vararg readings: VitalSignReading) = VitalHistoryUiState(
        readings = readings.toList(),
        ranges = mapOf(
            VitalSignType.HR to 60.0..100.0,
            VitalSignType.BP_SYS to 90.0..140.0,
            VitalSignType.BP_DIA to 60.0..90.0
        ),
        filter = filter,
        period = period,
        today = today,
        zone = lima
    )

    @Test
    fun `empty week has no average instead of NaN`() {
        val state = state()
        assertNull(state.average(VitalSignType.HR))
        assertNull(state.min(VitalSignType.HR))
        assertEquals(List(7) { null }, state.chartValues(VitalSignType.HR))
    }

    @Test
    fun `stats only use their own type`() {
        val state = state(
            reading(VitalSignType.HR, 72.0, "2026-10-01T15:00:00Z"),
            reading(VitalSignType.HR, 80.0, "2026-10-02T15:00:00Z"),
            reading(VitalSignType.SPO2, 98.0, "2026-10-02T15:00:00Z")
        )
        assertEquals(76.0, state.average(VitalSignType.HR)!!, 0.001)
        assertEquals(72.0, state.min(VitalSignType.HR)!!, 0.001)
        assertEquals(80.0, state.max(VitalSignType.HR)!!, 0.001)
    }

    @Test
    fun `evening reading in Lima belongs to the local day, not the UTC one`() {
        // 2026-10-06T01:35Z is 20:35 on 2026-10-05 in Lima
        val state = state(reading(VitalSignType.HR, 70.0, "2026-10-06T01:35:00Z"))
        assertEquals(70.0, state.chartValues(VitalSignType.HR)[5]!!, 0.001)
        assertNull(state.chartValues(VitalSignType.HR)[6])
    }

    @Test
    fun `readings outside the local week are left out`() {
        // 2026-09-30T04:00Z is still 2026-09-29 in Lima, one day before the week starts
        val state = state(
            reading(VitalSignType.HR, 200.0, "2026-09-30T04:00:00Z"),
            reading(VitalSignType.HR, 70.0, "2026-09-30T15:00:00Z")
        )
        assertEquals(70.0, state.average(VitalSignType.HR)!!, 0.001)
    }

    @Test
    fun `recent readings are newest first and hide the diastolic half`() {
        val at = "2026-10-05T20:00:00Z"
        val systolic = reading(VitalSignType.BP_SYS, 122.0, at)
        val state = state(
            reading(VitalSignType.HR, 78.0, "2026-10-05T10:00:00Z"),
            reading(VitalSignType.SPO2, 98.0, "2026-10-05T15:00:00Z"),
            systolic,
            reading(VitalSignType.BP_DIA, 80.0, at)
        )
        assertEquals(
            listOf(VitalSignType.BP_SYS, VitalSignType.SPO2, VitalSignType.HR),
            state.recentReadings.map { it.type }
        )
        assertEquals(80.0, state.diastolicFor(systolic)!!.value, 0.001)
    }

    @Test
    fun `blood pressure row is out of range when its diastolic half is`() {
        val at = "2026-10-05T20:00:00Z"
        val systolic = reading(VitalSignType.BP_SYS, 122.0, at)
        val state = state(systolic, reading(VitalSignType.BP_DIA, 95.0, at))
        assertFalse(state.isWithinRange(systolic))
    }

    @Test
    fun `reading without a known range counts as within range`() {
        assertTrue(state().isWithinRange(reading(VitalSignType.TEMP, 40.0, "2026-10-05T20:00:00Z")))
    }

    @Test
    fun `a day is drawn hour by hour on the local clock`() {
        // 2026-10-06T19:10Z and 19:50Z are 14:10 and 14:50 in Lima; 2026-10-06T03:00Z is still yesterday there
        val state = filtered(
            VitalFilter(),
            HistoryPeriod.DAY,
            reading(VitalSignType.HR, 70.0, "2026-10-06T19:10:00Z"),
            reading(VitalSignType.HR, 80.0, "2026-10-06T19:50:00Z"),
            reading(VitalSignType.HR, 99.0, "2026-10-06T03:00:00Z")
        )
        val values = state.chartValues(VitalSignType.HR)
        assertEquals(24, values.size)
        assertEquals(75.0, values[14]!!, 0.001)
        assertEquals(1, values.count { it != null })
    }

    @Test
    fun `a month has one point per day`() {
        val state = filtered(VitalFilter(), HistoryPeriod.MONTH, reading(VitalSignType.HR, 70.0, "2026-09-10T15:00:00Z"))
        assertEquals(30, state.chartValues(VitalSignType.HR).size)
        assertEquals(70.0, state.average(VitalSignType.HR)!!, 0.001)
    }

    @Test
    fun `charts follow the filtered types`() {
        val filter = VitalFilter().toggle(VitalFilterOption.TEMPERATURE).toggle(VitalFilterOption.HEART_RATE)
        assertEquals(listOf(VitalSignType.HR, VitalSignType.TEMP), filtered(filter, HistoryPeriod.WEEK).chartTypes)
        assertEquals(5, state().chartTypes.size)
    }

    @Test
    fun `recent readings follow the filtered types and state`() {
        val filter = VitalFilter().toggle(VitalFilterOption.HEART_RATE).toggle(VitalFilterOption.OBSERVATION)
        val state = filtered(
            filter,
            HistoryPeriod.WEEK,
            reading(VitalSignType.HR, 130.0, "2026-10-05T10:00:00Z"),
            reading(VitalSignType.HR, 78.0, "2026-10-05T11:00:00Z"),
            reading(VitalSignType.BP_SYS, 190.0, "2026-10-05T12:00:00Z")
        )
        assertEquals(listOf(130.0), state.recentReadings.map { it.value })
    }
}
