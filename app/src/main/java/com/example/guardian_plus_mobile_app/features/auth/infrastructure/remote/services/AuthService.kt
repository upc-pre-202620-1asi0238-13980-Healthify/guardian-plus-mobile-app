package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.AuthenticatedSessionDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.LoginRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.LoginResponseDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.MessageDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.RegisterUserCredentialsRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.RequestPasswordResetRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.ResendEmailVerificationRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.ResendOtpRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.ResetPasswordRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.UserAccountDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.VerifyEmailRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.VerifyOtpRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * /auth endpoints as the report designs them. The platform does not expose IAM yet, so these paths are
 * placeholders until it does; meanwhile the app signs in through SimulatedAuthRepository.
 */
interface AuthService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterUserCredentialsRequestDto): Response<UserAccountDto>

    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequestDto): Response<UserAccountDto>

    @POST("auth/verify-email/resend")
    suspend fun resendEmailVerification(@Body request: ResendEmailVerificationRequestDto): Response<MessageDto>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @POST("auth/otp/verify")
    suspend fun verifyOtp(@Body request: VerifyOtpRequestDto): Response<AuthenticatedSessionDto>

    // Not in the report's AuthController yet: the OTP screen of the prototype lets the user ask for a new code
    @POST("auth/otp/resend")
    suspend fun resendOtp(@Body request: ResendOtpRequestDto): Response<MessageDto>

    @POST("auth/password-reset/request")
    suspend fun requestPasswordReset(@Body request: RequestPasswordResetRequestDto): Response<MessageDto>

    @POST("auth/password-reset/confirm")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<MessageDto>
}
