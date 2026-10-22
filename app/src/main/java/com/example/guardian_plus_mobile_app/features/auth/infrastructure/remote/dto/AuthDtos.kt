package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto

// Bodies of the platform's AuthController (report, section 2.6.7.2)
data class RegisterUserCredentialsRequestDto(
    val email: String,
    val password: String
)

data class VerifyEmailRequestDto(
    val userAccountId: String,
    val code: String
)

data class ResendEmailVerificationRequestDto(
    val userAccountId: String
)

data class LoginRequestDto(
    val email: String,
    val password: String
)

/** Answer of /login: the session when it is complete, or which step is still missing. */
data class LoginResponseDto(
    val userAccountId: String,
    val emailVerified: Boolean,
    val twoFactorRequired: Boolean,
    val token: String?
)

data class VerifyOtpRequestDto(
    val userAccountId: String,
    val code: String
)

data class ResendOtpRequestDto(
    val userAccountId: String
)

// AuthenticatedSessionResource
data class AuthenticatedSessionDto(
    val userAccountId: String,
    val token: String
)

data class RequestPasswordResetRequestDto(
    val email: String
)

data class ResetPasswordRequestDto(
    val token: String,
    val newPassword: String
)

/** Acknowledgement of the endpoints that only trigger an email. */
data class MessageDto(
    val message: String
)
