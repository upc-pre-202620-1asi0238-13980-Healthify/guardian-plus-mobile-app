package com.example.guardian_plus_mobile_app.features.auth.presentation

import com.example.guardian_plus_mobile_app.features.auth.domain.model.UserAccount


data class LoginUiState(
val email: String = "",
    val password: String = "",
    val isPasswordHidden: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val user: UserAccount? = null,
    val isAuthenticated: Boolean = false
)

