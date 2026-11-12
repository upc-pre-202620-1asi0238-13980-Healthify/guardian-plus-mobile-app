package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.stock

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ParseUnitsAddedTest {

    @Test
    fun `a whole number of units is accepted`() {
        assertEquals(30, parseUnitsAdded("30"))
    }

    @Test
    fun `surrounding spaces are ignored`() {
        assertEquals(12, parseUnitsAdded(" 12 "))
    }

    @Test
    fun `the largest allowed quantity is accepted`() {
        assertEquals(MAX_UNITS_PER_ACQUISITION, parseUnitsAdded(MAX_UNITS_PER_ACQUISITION.toString()))
    }

    @Test
    fun `an empty field is rejected`() {
        assertNull(parseUnitsAdded(""))
    }

    @Test
    fun `zero units are rejected`() {
        assertNull(parseUnitsAdded("0"))
    }

    @Test
    fun `a negative quantity is rejected`() {
        assertNull(parseUnitsAdded("-5"))
    }

    @Test
    fun `a quantity above the maximum is rejected`() {
        assertNull(parseUnitsAdded((MAX_UNITS_PER_ACQUISITION + 1).toString()))
    }

    @Test
    fun `a decimal quantity is rejected`() {
        assertNull(parseUnitsAdded("2.5"))
    }
}
