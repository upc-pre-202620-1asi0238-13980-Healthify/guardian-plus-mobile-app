package com.example.guardian_plus_mobile_app.features.auth.domain

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import java.time.Instant
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthenticatedSessionTest {

    private val expiresAt = Instant.parse("2026-10-22T20:00:00Z")
    private val session = AuthenticatedSession("id", "jwt", expiresAt)

    @Test
    fun `a session is valid until it expires`() {
        assertFalse(session.isExpired(expiresAt.minusSeconds(1)))
    }

    @Test
    fun `a session is expired from its expiration on`() {
        assertTrue(session.isExpired(expiresAt))
        assertTrue(session.isExpired(expiresAt.plusSeconds(60)))
    }
}
