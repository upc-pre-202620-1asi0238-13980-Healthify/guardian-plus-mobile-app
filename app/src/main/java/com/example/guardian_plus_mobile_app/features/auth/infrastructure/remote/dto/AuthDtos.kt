package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto

// Bodies of the platform's AuthController (/api/v1/auth)
// fullName is not part of IAM's credentials; the platform ignores it until Profile takes it from IAM
data class RegisterUserCredentialsRequestDto(
    val fullName: String,
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

/** Answer of a right email and password (202): the OTP was emailed to the account. */
data class OtpChallengeDto(
    val userAccountId: String,
    val message: String?
)

data class VerifyOtpRequestDto(
    val userAccountId: String,
    val code: String
)

data class ResendOtpRequestDto(
    val userAccountId: String
)

// AuthenticatedSessionResource; expiresAt is an ISO-8601 instant
data class AuthenticatedSessionDto(
    val userAccountId: String,
    val accessToken: String,
    val tokenType: String?,
    val expiresAt: String
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

/**
 * Error body of the platform (ErrorResource). IAM puts the user account id in [details] when a right password
 * belongs to an account whose email is not verified yet.
 */
data class AuthErrorDto(
    val code: String?,
    val message: String?,
    val details: String?
)
