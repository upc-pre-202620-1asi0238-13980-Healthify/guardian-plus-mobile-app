package com.example.guardian_plus_mobile_app.features.auth.presentation.common

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email

/** What the user typed as an [Email], or null when it is not a valid address. */
fun parseEmail(input: String): Email? = runCatching { Email(input.trim()) }.getOrNull()

/** IAM's minimum for a new password. */
const val MIN_PASSWORD_LENGTH = 8

/** Hides most of the local part, as the OTP screen shows where the code went: maria@correo.com → ma••••@correo.com. */
fun maskEmail(email: String): String {
    val at = email.indexOf('@')
    if (at <= 0) return email
    val visible = email.take(if (at > 2) 2 else 1)
    return "$visible••••${email.substring(at)}"
}
