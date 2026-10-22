package com.example.guardian_plus_mobile_app.features.auth.presentation.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EmailInputTest {

    @Test
    fun `masks the local part keeping its first two letters`() {
        assertEquals("ma••••@correo.com", maskEmail("maria@correo.com"))
    }

    @Test
    fun `a very short local part only keeps its first letter`() {
        assertEquals("j••••@correo.com", maskEmail("jo@correo.com"))
    }

    @Test
    fun `parses the typed email ignoring surrounding spaces`() {
        assertEquals("maria@correo.com", parseEmail("  maria@correo.com ")?.value)
    }

    @Test
    fun `an invalid email parses to null`() {
        assertNull(parseEmail("maria@"))
    }
}
