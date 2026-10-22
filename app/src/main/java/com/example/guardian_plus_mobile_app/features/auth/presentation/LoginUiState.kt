package com.example.guardian_plus_mobile_app.features.auth.presentation

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordHidden: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Next step once the credentials are accepted; the screen navigates and clears it
    val navigation: LoginNavigation? = null
)

sealed interface LoginNavigation {
    data class TwoFactor(val userAccountId: String, val email: String) : LoginNavigation

    data class VerifyEmail(val userAccountId: String, val email: String) : LoginNavigation
}
