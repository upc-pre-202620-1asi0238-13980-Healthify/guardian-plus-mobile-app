package com.example.guardian_plus_mobile_app.features.auth.infrastructure

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.UserAccountStatus
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.AuthenticatedSessionDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.OtpChallengeDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.UserAccountDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.toDomain
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class UserAccountMappersTest {

    @Test
    fun `maps the user account resource`() {
        val account = UserAccountDto("id", "maria@correo.com", "ACTIVE", emailVerified = true).toDomain()

        assertEquals("maria@correo.com", account.email.value)
        assertEquals(UserAccountStatus.ACTIVE, account.status)
    }

    @Test
    fun `a right password always leads to the OTP challenge`() {
        val outcome = OtpChallengeDto("id", "Se envió un código de inicio de sesión a tu correo").toDomain()

        assertEquals(LoginOutcome.SecondFactorRequired("id"), outcome)
    }

    @Test
    fun `maps the session with its expiration`() {
        val session = AuthenticatedSessionDto("id", "jwt", "Bearer", "2026-10-22T20:00:00Z").toDomain()

        assertEquals("jwt", session.accessToken)
        assertEquals(Instant.parse("2026-10-22T20:00:00Z"), session.expiresAt)
    }
}
