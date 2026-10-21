package com.example.guardian_plus_mobile_app.features.auth.presentation.twofactor

data class TwoFactorUiState(
    val code: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)
