package com.example.guardian_plus_mobile_app.features.auth.domain

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailTest {

    @Test
    fun `accepts a well formed address`() {
        assertEquals("maria.rojas@guardianplus.pe", Email("maria.rojas@guardianplus.pe").value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects an address without domain`() {
        Email("maria.rojas@")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects an address without at`() {
        Email("maria.rojas.guardianplus.pe")
    }

    @Test
    fun `matches ignoring case and surrounding spaces`() {
        assertTrue(Email("maria@correo.com").matches("  Maria@Correo.com "))
    }

    @Test
    fun `does not match another address`() {
        assertFalse(Email("maria@correo.com").matches("elena@correo.com"))
    }
}
