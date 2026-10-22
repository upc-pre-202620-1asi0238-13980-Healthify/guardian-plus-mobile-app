package com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects

import java.time.Instant

/**
 * Signed JWT issued by IAM once the whole sign-in flow succeeds (password and OTP), and the account it belongs to.
 * The platform stops accepting it at [expiresAt], so an expired session is as good as none.
 */
data class AuthenticatedSession(
    val userAccountId: String,
    val accessToken: String,
    val expiresAt: Instant
) {
    fun isExpired(now: Instant = Instant.now()): Boolean = !now.isBefore(expiresAt)
}
