package com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.UserAccountStatus

/**
 * Digital identity of a caregiver or family member, as the platform's UserAccountResource publishes it.
 * The password hash never leaves IAM, so the app only knows the public side of the account.
 */
data class UserAccount(
    val id: String,
    val email: Email,
    val emailVerified: Boolean,
    val status: UserAccountStatus
) {
    fun canAuthenticate(): Boolean = emailVerified && status.canAuthenticate()
}
