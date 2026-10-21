package com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects

/** Signed JWT issued by IAM once the whole sign-in flow succeeds, and the account it belongs to. */
data class AuthenticatedSession(
    val userAccountId: String,
    val accessToken: String
)
