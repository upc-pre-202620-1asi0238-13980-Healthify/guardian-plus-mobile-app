package com.example.guardian_plus_mobile_app.features.auth.domain.repositories

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome

interface AuthRepository {
    /** The account starts pending, and IAM emails it an EMAIL_VERIFICATION code. */
    suspend fun register(email: Email, rawPassword: String): Result<UserAccount>

    suspend fun verifyEmail(userAccountId: String, code: String): Result<UserAccount>

    suspend fun resendEmailVerification(userAccountId: String): Result<Unit>

    suspend fun login(email: Email, rawPassword: String): Result<LoginOutcome>

    /** Checks the LOGIN code of an account with two-factor enabled and returns the session. */
    suspend fun verifyOtp(userAccountId: String, code: String): Result<AuthenticatedSession>

    /** Issues a new LOGIN code; the previous one stops being usable. */
    suspend fun resendOtp(userAccountId: String): Result<Unit>

    /** Succeeds whether or not the email belongs to an account, so it never reveals which ones exist. */
    suspend fun requestPasswordReset(email: Email): Result<Unit>

    suspend fun resetPassword(token: String, newRawPassword: String): Result<Unit>
}
