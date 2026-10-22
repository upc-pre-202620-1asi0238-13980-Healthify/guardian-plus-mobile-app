package com.example.guardian_plus_mobile_app.features.auth.infrastructure

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.simulated.SimulatedAuthRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.simulated.SimulatedIamStore
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.simulated.SimulatedIamStore.Companion.SIMULATED_CODE
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulatedAuthRepositoryTest {

    private val repository = SimulatedAuthRepository(SimulatedIamStore())
    private val email = Email("nuevo@correo.com")

    @Test
    fun `a new account must verify its email before signing in`() = runBlocking {
        val account = repository.register("María Rodríguez", email, "Secreta123").getOrThrow()

        assertEquals(LoginOutcome.EmailVerificationRequired(account.id), repository.login(email, "Secreta123").getOrThrow())

        repository.verifyEmail(account.id, SIMULATED_CODE).getOrThrow()
        assertTrue(repository.login(email, "Secreta123").getOrThrow() is LoginOutcome.Authenticated)
    }

    @Test
    fun `the same email cannot be registered twice`() = runBlocking {
        repository.register("María Rodríguez", email, "Secreta123").getOrThrow()

        assertTrue(repository.register("María Rodríguez", Email("NUEVO@correo.com"), "Otra12345").isFailure)
    }

    @Test
    fun `a wrong password is rejected`() = runBlocking {
        repository.register("María Rodríguez", email, "Secreta123").getOrThrow()

        assertTrue(repository.login(email, "incorrecta").isFailure)
    }

    @Test
    fun `the demo caregiver goes through the OTP challenge`() = runBlocking {
        val demoEmail = Email(SimulatedIamStore.DEMO_EMAIL)
        val outcome = repository.login(demoEmail, SimulatedIamStore.DEMO_PASSWORD).getOrThrow()

        val userAccountId = (outcome as LoginOutcome.SecondFactorRequired).userAccountId
        assertTrue(repository.verifyOtp(userAccountId, "000000").isFailure)
        assertEquals(userAccountId, repository.verifyOtp(userAccountId, SIMULATED_CODE).getOrThrow().userAccountId)
    }
}
