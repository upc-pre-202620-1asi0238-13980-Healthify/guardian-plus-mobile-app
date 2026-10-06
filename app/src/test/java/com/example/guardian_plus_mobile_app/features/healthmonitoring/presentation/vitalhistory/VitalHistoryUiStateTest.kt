package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
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

    private fun state(vararg readings: VitalSignReading) = VitalHistoryUiState(
        readings = readings.toList(),
        ranges = mapOf(
            VitalSignType.HR to 60.0..100.0,
            VitalSignType.BP_SYS to 90.0..140.0,
            VitalSignType.BP_DIA to 60.0..90.0
        ),
        today = today,
        zone = lima
    )

    @Test
    fun `empty week has no average instead of NaN`() {
        val state = state()
        assertNull(state.weeklyAverage)
        assertNull(state.weeklyMin)
        assertEquals(List(7) { null }, state.dailyAverages)
    }

    @Test
    fun `weekly stats only use the selected type`() {
        val state = state(
            reading(VitalSignType.HR, 72.0, "2026-10-01T15:00:00Z"),
            reading(VitalSignType.HR, 80.0, "2026-10-02T15:00:00Z"),
            reading(VitalSignType.SPO2, 98.0, "2026-10-02T15:00:00Z")
        )
        assertEquals(76.0, state.weeklyAverage!!, 0.001)
        assertEquals(72.0, state.weeklyMin!!, 0.001)
        assertEquals(80.0, state.weeklyMax!!, 0.001)
    }

    @Test
    fun `evening reading in Lima belongs to the local day, not the UTC one`() {
        // 2026-10-06T01:35Z is 20:35 on 2026-10-05 in Lima
        val state = state(reading(VitalSignType.HR, 70.0, "2026-10-06T01:35:00Z"))
        assertEquals(70.0, state.dailyAverages[5]!!, 0.001)
        assertNull(state.dailyAverages[6])
    }

    @Test
    fun `readings outside the local week are left out`() {
        // 2026-09-30T04:00Z is still 2026-09-29 in Lima, one day before the week starts
        val state = state(
            reading(VitalSignType.HR, 200.0, "2026-09-30T04:00:00Z"),
            reading(VitalSignType.HR, 70.0, "2026-09-30T15:00:00Z")
        )
        assertEquals(70.0, state.weeklyAverage!!, 0.001)
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
}
