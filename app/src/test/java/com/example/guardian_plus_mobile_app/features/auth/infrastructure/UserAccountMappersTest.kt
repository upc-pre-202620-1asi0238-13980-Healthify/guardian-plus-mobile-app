package com.example.guardian_plus_mobile_app.features.auth.infrastructure

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.UserAccountStatus
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.LoginResponseDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.UserAccountDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.toDomain
import org.junit.Assert.assertEquals
import org.junit.Test

class UserAccountMappersTest {

    @Test
    fun `maps the user account resource`() {
        val account = UserAccountDto("id", "maria@correo.com", "ACTIVE", emailVerified = true, twoFactorEnabled = false).toDomain()

        assertEquals("maria@correo.com", account.email.value)
        assertEquals(UserAccountStatus.ACTIVE, account.status)
    }

    @Test
    fun `an unverified email leads to the verification`() {
        val outcome = LoginResponseDto("id", emailVerified = false, twoFactorRequired = false, token = null).toDomain()

        assertEquals(LoginOutcome.EmailVerificationRequired("id"), outcome)
    }

    @Test
    fun `two factor leads to the OTP challenge`() {
        val outcome = LoginResponseDto("id", emailVerified = true, twoFactorRequired = true, token = null).toDomain()

        assertEquals(LoginOutcome.SecondFactorRequired("id"), outcome)
    }

    @Test
    fun `a complete login carries the session`() {
        val outcome = LoginResponseDto("id", emailVerified = true, twoFactorRequired = false, token = "jwt").toDomain()

        assertEquals("jwt", (outcome as LoginOutcome.Authenticated).session.accessToken)
    }
}
