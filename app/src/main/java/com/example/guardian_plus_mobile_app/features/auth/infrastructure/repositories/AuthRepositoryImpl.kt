package com.example.guardian_plus_mobile_app.features.auth.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.AuthErrorDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.LoginRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.RegisterUserCredentialsRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.RequestPasswordResetRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.ResendEmailVerificationRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.ResendOtpRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.ResetPasswordRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.VerifyEmailRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.VerifyOtpRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.services.AuthService
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.toDomain
import com.google.gson.Gson
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService
) : AuthRepository {

    override suspend fun register(fullName: String, email: Email, rawPassword: String): Result<UserAccount> =
        apiCall({ service.register(RegisterUserCredentialsRequestDto(fullName, email.value, rawPassword)) }) { it.toDomain() }

    override suspend fun verifyEmail(userAccountId: String, code: String): Result<UserAccount> =
        apiCall({ service.verifyEmail(VerifyEmailRequestDto(userAccountId, code)) }) { it.toDomain() }

    override suspend fun resendEmailVerification(userAccountId: String): Result<Unit> =
        apiCall({ service.resendEmailVerification(ResendEmailVerificationRequestDto(userAccountId)) }) { }

    /**
     * A right password answers 202 and the OTP is on its way. A right password on an unverified account answers
     * 403 with the account id in the error details, so the sign-in can continue with the email verification.
     */
    override suspend fun login(email: Email, rawPassword: String): Result<LoginOutcome> = try {
        val response = service.login(LoginRequestDto(email.value, rawPassword))
        val challenge = response.body()
        when {
            response.isSuccessful && challenge != null -> Result.success(challenge.toDomain())
            response.isSuccessful -> Result.failure(Exception("El servidor no devolvió datos"))
            else -> {
                val error = readError(response.errorBody()?.string())
                val userAccountId = error?.details
                if (error?.code == EMAIL_NOT_VERIFIED && !userAccountId.isNullOrBlank()) {
                    Result.success(LoginOutcome.EmailVerificationRequired(userAccountId))
                } else {
                    Result.failure(Exception(error?.message ?: "El servidor respondió con el código ${response.code()}"))
                }
            }
        }
    } catch (_: IOException) {
        Result.failure(Exception("No se pudo conectar con el servidor. Revisa tu conexión."))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(Exception("El servidor devolvió datos que la app no reconoce: ${e.message}"))
    }

    override suspend fun verifyOtp(userAccountId: String, code: String): Result<AuthenticatedSession> =
        apiCall({ service.verifyOtp(VerifyOtpRequestDto(userAccountId, code)) }) { it.toDomain() }

    override suspend fun resendOtp(userAccountId: String): Result<Unit> =
        apiCall({ service.resendOtp(ResendOtpRequestDto(userAccountId)) }) { }

    override suspend fun requestPasswordReset(email: Email): Result<Unit> =
        apiCall({ service.requestPasswordReset(RequestPasswordResetRequestDto(email.value)) }) { }

    override suspend fun resetPassword(token: String, newRawPassword: String): Result<Unit> =
        apiCall({ service.resetPassword(ResetPasswordRequestDto(token, newRawPassword)) }) { }

    private fun readError(body: String?): AuthErrorDto? = try {
        body?.let { Gson().fromJson(it, AuthErrorDto::class.java) }
    } catch (_: Exception) {
        null
    }

    private companion object {
        const val EMAIL_NOT_VERIFIED = "EMAIL_NOT_VERIFIED"
    }
}
