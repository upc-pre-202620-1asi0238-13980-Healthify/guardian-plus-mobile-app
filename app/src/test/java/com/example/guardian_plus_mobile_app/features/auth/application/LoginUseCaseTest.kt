package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUseCaseTest {

    /** Answers every login with [loginOutcome] and records the verification codes it is asked to resend. */
    private class FakeAuthRepository(private val loginOutcome: Result<LoginOutcome>) : AuthRepository {
        val resentVerifications = mutableListOf<String>()

        override suspend fun login(email: Email, rawPassword: String) = loginOutcome

        override suspend fun resendEmailVerification(userAccountId: String): Result<Unit> {
            resentVerifications += userAccountId
            return Result.success(Unit)
        }

        override suspend fun register(fullName: String, email: Email, rawPassword: String): Result<UserAccount> = error("unused")
        override suspend fun verifyEmail(userAccountId: String, code: String): Result<UserAccount> = error("unused")
        override suspend fun verifyOtp(userAccountId: String, code: String): Result<AuthenticatedSession> = error("unused")
        override suspend fun resendOtp(userAccountId: String): Result<Unit> = error("unused")
        override suspend fun requestPasswordReset(email: Email): Result<Unit> = error("unused")
        override suspend fun resetPassword(token: String, newRawPassword: String): Result<Unit> = error("unused")
    }

    private val email = Email("maria@correo.com")

    @Test
    fun `a right password continues with the OTP`() = runBlocking {
        val repository = FakeAuthRepository(Result.success(LoginOutcome.SecondFactorRequired("id")))

        val outcome = LoginUseCase(repository)(email, "Guardian2026!")

        assertEquals(LoginOutcome.SecondFactorRequired("id"), outcome.getOrNull())
        assertTrue(repository.resentVerifications.isEmpty())
    }

    @Test
    fun `an unverified account gets a fresh verification code`() = runBlocking {
        val repository = FakeAuthRepository(Result.success(LoginOutcome.EmailVerificationRequired("id")))

        LoginUseCase(repository)(email, "Guardian2026!")

        assertEquals(listOf("id"), repository.resentVerifications)
    }
}
