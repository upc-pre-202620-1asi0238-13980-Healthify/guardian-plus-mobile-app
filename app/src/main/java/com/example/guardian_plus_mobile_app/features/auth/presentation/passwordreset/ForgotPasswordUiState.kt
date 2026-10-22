package com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // The recovery code was emailed
    val isSent: Boolean = false
)
