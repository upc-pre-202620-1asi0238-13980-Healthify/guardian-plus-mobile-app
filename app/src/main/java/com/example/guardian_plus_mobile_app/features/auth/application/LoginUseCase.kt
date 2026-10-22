package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

/**
 * First step of the sign-in. No session comes out of it: the platform emails the OTP and the session is issued
 * once that code is verified ([VerifyOtpUseCase]).
 */
class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: Email, rawPassword: String): Result<LoginOutcome> =
        repository.login(email, rawPassword).onSuccess { outcome ->
            // The code sent at sign-up may be long expired, so an unverified account gets a fresh one. If that
            // fails (e.g. too many codes), the verification screen still lets the user ask again
            if (outcome is LoginOutcome.EmailVerificationRequired) {
                repository.resendEmailVerification(outcome.userAccountId)
            }
        }
}
