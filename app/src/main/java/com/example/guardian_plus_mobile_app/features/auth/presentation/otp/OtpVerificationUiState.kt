package com.example.guardian_plus_mobile_app.features.auth.presentation.otp

import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AUTH_CODE_LENGTH

/** Seconds before a new code can be requested, so the inbox is not flooded. */
const val RESEND_COOLDOWN_SEC = 30

data class OtpVerificationUiState(
    val code: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resendAvailableInSec: Int = RESEND_COOLDOWN_SEC,
    val isResending: Boolean = false,
    // One-off notice after a resend, shown in a snackbar
    val actionMessage: String? = null,
    val isVerified: Boolean = false
) {
    val isComplete: Boolean get() = code.length == AUTH_CODE_LENGTH

    val canResend: Boolean get() = resendAvailableInSec == 0 && !isResending

    /** Countdown as the prototype shows it, e.g. 0:16. */
    val resendCountdown: String get() = "%d:%02d".format(resendAvailableInSec / 60, resendAvailableInSec % 60)
}
