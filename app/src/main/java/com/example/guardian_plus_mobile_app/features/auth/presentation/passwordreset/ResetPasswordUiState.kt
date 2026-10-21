package com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset

data class ResetPasswordUiState(
    val token: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordHidden: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isReset: Boolean = false
)
