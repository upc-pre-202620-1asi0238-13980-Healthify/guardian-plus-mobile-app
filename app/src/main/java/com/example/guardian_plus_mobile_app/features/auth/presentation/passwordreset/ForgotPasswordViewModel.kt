package com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.auth.application.RequestPasswordResetUseCase
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.parseEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val requestPasswordReset: RequestPasswordResetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun requestReset() {
        val state = _uiState.value
        if (state.isLoading) return
        val email = parseEmail(state.email)
        if (email == null) {
            _uiState.update { it.copy(errorMessage = "Ingresa un correo electrónico válido") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            requestPasswordReset(email)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSent = true) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo enviar el código") }
                }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isSent = false) }
    }
}
