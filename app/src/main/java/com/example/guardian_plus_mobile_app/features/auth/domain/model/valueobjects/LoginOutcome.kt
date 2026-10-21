package com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects

/** Where valid credentials lead: straight into the app, to the OTP challenge or to the email verification. */
sealed interface LoginOutcome {
    data class Authenticated(val session: AuthenticatedSession) : LoginOutcome

    data class SecondFactorRequired(val userAccountId: String) : LoginOutcome

    data class EmailVerificationRequired(val userAccountId: String) : LoginOutcome
}
