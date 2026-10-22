package com.example.guardian_plus_mobile_app.features.auth.presentation.register

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordHidden: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Set once the account exists; the screen moves on to the email verification and clears it
    val registeredAccount: RegisteredAccount? = null
)

data class RegisteredAccount(val userAccountId: String, val email: String)
