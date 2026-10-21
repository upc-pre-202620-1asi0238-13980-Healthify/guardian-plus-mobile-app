package com.example.guardian_plus_mobile_app.features.auth.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.LoginRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.RegisterUserCredentialsRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.RequestPasswordResetRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.ResendEmailVerificationRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.ResetPasswordRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.VerifyEmailRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.VerifyOtpRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.services.AuthService
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.toDomain
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService
) : AuthRepository {

    override suspend fun register(email: Email, rawPassword: String): Result<UserAccount> =
        apiCall({ service.register(RegisterUserCredentialsRequestDto(email.value, rawPassword)) }) { it.toDomain() }

    override suspend fun verifyEmail(userAccountId: String, code: String): Result<UserAccount> =
        apiCall({ service.verifyEmail(VerifyEmailRequestDto(userAccountId, code)) }) { it.toDomain() }

    override suspend fun resendEmailVerification(userAccountId: String): Result<Unit> =
        apiCall({ service.resendEmailVerification(ResendEmailVerificationRequestDto(userAccountId)) }) { }

    override suspend fun login(email: Email, rawPassword: String): Result<LoginOutcome> =
        apiCall({ service.login(LoginRequestDto(email.value, rawPassword)) }) { it.toDomain() }

    override suspend fun verifyOtp(userAccountId: String, code: String): Result<AuthenticatedSession> =
        apiCall({ service.verifyOtp(VerifyOtpRequestDto(userAccountId, code)) }) { it.toDomain() }

    override suspend fun requestPasswordReset(email: Email): Result<Unit> =
        apiCall({ service.requestPasswordReset(RequestPasswordResetRequestDto(email.value)) }) { }

    override suspend fun resetPassword(token: String, newRawPassword: String): Result<Unit> =
        apiCall({ service.resetPassword(ResetPasswordRequestDto(token, newRawPassword)) }) { }
}
