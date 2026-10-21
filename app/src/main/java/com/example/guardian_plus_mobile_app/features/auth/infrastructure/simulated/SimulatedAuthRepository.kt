package com.example.guardian_plus_mobile_app.features.auth.infrastructure.simulated

import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.UserAccountStatus
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.simulated.SimulatedIamStore.Companion.SIMULATED_CODE
import javax.inject.Inject

/**
 * Sign-in flow served locally until the platform exposes /auth. Registered accounts follow IAM's rules;
 * an email nobody registered still signs in as the demo caregiver, as the prototype's login did.
 */
class SimulatedAuthRepository @Inject constructor(
    private val store: SimulatedIamStore
) : AuthRepository {

    override suspend fun register(email: Email, rawPassword: String): Result<UserAccount> =
        if (store.findByEmail(email) != null) {
            Result.failure(Exception("Ya existe una cuenta con este correo"))
        } else {
            Result.success(store.register(email, rawPassword))
        }

    override suspend fun verifyEmail(userAccountId: String, code: String): Result<UserAccount> {
        val stored = store.findById(userAccountId) ?: return Result.failure(Exception("La cuenta no existe"))
        if (code != SIMULATED_CODE) return Result.failure(Exception("El código no es válido o ya venció"))
        val verified = stored.account.copy(emailVerified = true, status = UserAccountStatus.ACTIVE)
        store.update(verified)
        return Result.success(verified)
    }

    override suspend fun resendEmailVerification(userAccountId: String): Result<Unit> =
        if (store.findById(userAccountId) != null) Result.success(Unit) else Result.failure(Exception("La cuenta no existe"))

    override suspend fun login(email: Email, rawPassword: String): Result<LoginOutcome> {
        val stored = store.findByEmail(email)
            ?: return Result.success(LoginOutcome.Authenticated(sessionFor(DemoSession.CURRENT_USER_ID)))
        val account = stored.account
        return when {
            stored.password != rawPassword -> Result.failure(Exception("Correo o contraseña incorrectos"))
            !account.canAuthenticate() -> Result.success(LoginOutcome.EmailVerificationRequired(account.id))
            account.requiresSecondFactor() -> Result.success(LoginOutcome.SecondFactorRequired(account.id))
            else -> Result.success(LoginOutcome.Authenticated(sessionFor(account.id)))
        }
    }

    override suspend fun verifyOtp(userAccountId: String, code: String): Result<AuthenticatedSession> = when {
        store.findById(userAccountId) == null -> Result.failure(Exception("La cuenta no existe"))
        code != SIMULATED_CODE -> Result.failure(Exception("El código no es válido o ya venció"))
        else -> Result.success(sessionFor(userAccountId))
    }

    override suspend fun requestPasswordReset(email: Email): Result<Unit> = Result.success(Unit)

    // The token is the code of the email; with no account to tie it to, it resets the demo caregiver's
    override suspend fun resetPassword(token: String, newRawPassword: String): Result<Unit> {
        if (token != SIMULATED_CODE) return Result.failure(Exception("El enlace de recuperación no es válido o ya venció"))
        store.findById(DemoSession.CURRENT_USER_ID)?.let { store.update(it.account, password = newRawPassword) }
        return Result.success(Unit)
    }

    private fun sessionFor(userAccountId: String) =
        AuthenticatedSession(userAccountId = userAccountId, accessToken = "simulated-token-$userAccountId")
}
