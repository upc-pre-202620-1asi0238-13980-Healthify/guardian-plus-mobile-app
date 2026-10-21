package com.example.guardian_plus_mobile_app.features.auth.presentation.verifyemail

data class VerifyEmailUiState(
    val code: String = "",
    val isLoading: Boolean = false,
    val isResending: Boolean = false,
    val errorMessage: String? = null,
    // One-off notice after a resend, shown in a snackbar
    val actionMessage: String? = null,
    val isVerified: Boolean = false
)
