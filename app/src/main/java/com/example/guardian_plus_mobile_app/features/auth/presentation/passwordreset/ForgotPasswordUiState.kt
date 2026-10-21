package com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // The link was sent (or would have been, IAM does not say which emails exist)
    val isSent: Boolean = false
)
