package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VitalFilterTest {

    @Test
    fun `empty filter shows every vital sign of the week in any state`() {
        val filter = VitalFilter()
        assertEquals(displayedVitalTypes, filter.types)
        assertEquals(HistoryPeriod.WEEK, filter.period)
        assertTrue(filter.includesState(withinRange = true))
        assertTrue(filter.includesState(withinRange = false))
    }

    @Test
    fun `selected types keep the order of the screen`() {
        val filter = VitalFilter()
            .toggle(VitalFilterOption.TEMPERATURE)
            .toggle(VitalFilterOption.HEART_RATE)
        assertEquals(listOf(VitalSignType.HR, VitalSignType.TEMP), filter.types)
    }

    @Test
    fun `diastolic follows the blood pressure option`() {
        val filter = VitalFilter().toggle(VitalFilterOption.BLOOD_PRESSURE)
        assertTrue(filter.includes(VitalSignType.BP_DIA))
        assertFalse(filter.includes(VitalSignType.HR))
    }

    @Test
    fun `picking a period replaces the previous one`() {
        val filter = VitalFilter()
            .toggle(VitalFilterOption.DAY)
            .toggle(VitalFilterOption.HEART_RATE)
            .toggle(VitalFilterOption.MONTH)
        assertEquals(HistoryPeriod.MONTH, filter.period)
        assertEquals(setOf(VitalFilterOption.HEART_RATE, VitalFilterOption.MONTH), filter.options)
    }

    @Test
    fun `toggling twice removes the option`() {
        assertTrue(VitalFilter().toggle(VitalFilterOption.NORMAL).toggle(VitalFilterOption.NORMAL).isEmpty)
    }

    @Test
    fun `state options combine as either one`() {
        val normal = VitalFilter().toggle(VitalFilterOption.NORMAL)
        assertTrue(normal.includesState(withinRange = true))
        assertFalse(normal.includesState(withinRange = false))

        val both = normal.toggle(VitalFilterOption.OBSERVATION)
        assertTrue(both.includesState(withinRange = false))
    }

    @Test
    fun `filter survives being saved as names`() {
        val filter = VitalFilter()
            .toggle(VitalFilterOption.OXYGEN_SATURATION)
            .toggle(VitalFilterOption.DAY)
            .toggle(VitalFilterOption.OBSERVATION)
        assertEquals(filter, VitalFilter.fromNames(filter.options.map { it.name }))
    }
}
