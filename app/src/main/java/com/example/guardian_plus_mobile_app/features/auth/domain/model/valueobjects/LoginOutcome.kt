package com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects

/**
 * Where a right email and password lead. Every sign-in continues with the OTP emailed to the account (the IAM
 * EventStorming: Login, Credentials correct?, Trigger 2FA), unless the email is not verified yet.
 */
sealed interface LoginOutcome {
    data class SecondFactorRequired(val userAccountId: String) : LoginOutcome

    data class EmailVerificationRequired(val userAccountId: String) : LoginOutcome
}
