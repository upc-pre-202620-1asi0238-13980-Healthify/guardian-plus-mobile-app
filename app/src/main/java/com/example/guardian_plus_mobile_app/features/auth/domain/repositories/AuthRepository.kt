package com.example.guardian_plus_mobile_app.features.auth.domain.repositories

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome

interface AuthRepository {
    /**
     * The account starts pending, and IAM emails it an EMAIL_VERIFICATION code. The full name is not part of
     * the credentials: IAM forwards it to Profile, which creates the caregiver's profile.
     */
    suspend fun register(fullName: String, email: Email, rawPassword: String): Result<UserAccount>

    suspend fun verifyEmail(userAccountId: String, code: String): Result<UserAccount>

    suspend fun resendEmailVerification(userAccountId: String): Result<Unit>

    /** A right password always leads to the OTP emailed to the account, or to the email verification. */
    suspend fun login(email: Email, rawPassword: String): Result<LoginOutcome>

    /** Checks the LOGIN code of the sign-in in progress and returns the session. */
    suspend fun verifyOtp(userAccountId: String, code: String): Result<AuthenticatedSession>

    /** Issues a new LOGIN code; the previous one stops being usable. */
    suspend fun resendOtp(userAccountId: String): Result<Unit>

    /** Emails a recovery code; fails if no account uses the email. */
    suspend fun requestPasswordReset(email: Email): Result<Unit>

    suspend fun resetPassword(token: String, newRawPassword: String): Result<Unit>
}
