package com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.auth.application.ResetPasswordUseCase
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.MIN_PASSWORD_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPassword: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onTokenChange(token: String) {
        _uiState.update { it.copy(token = token, errorMessage = null) }
    }

    fun onNewPasswordChange(password: String) {
        _uiState.update { it.copy(newPassword = password, errorMessage = null) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordHidden = !it.isPasswordHidden) }
    }

    fun reset() {
        val state = _uiState.value
        if (state.isLoading) return
        val error = when {
            state.token.isBlank() || state.newPassword.isBlank() || state.confirmPassword.isBlank() ->
                "Completa todos los campos para continuar"
            state.newPassword.length < MIN_PASSWORD_LENGTH ->
                "La contraseña debe tener al menos $MIN_PASSWORD_LENGTH caracteres"
            state.newPassword != state.confirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            resetPassword(state.token.trim(), state.newPassword)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, newPassword = "", confirmPassword = "", isReset = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo restablecer la contraseña") }
                }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isReset = false) }
    }
}
