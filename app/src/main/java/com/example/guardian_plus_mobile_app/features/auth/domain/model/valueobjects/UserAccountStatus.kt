package com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects

/** Lifecycle of an account: it can only sign in once its email has been verified. */
enum class UserAccountStatus {
    PENDING_EMAIL_VERIFICATION,
    ACTIVE;

    fun canAuthenticate(): Boolean = this == ACTIVE
}
