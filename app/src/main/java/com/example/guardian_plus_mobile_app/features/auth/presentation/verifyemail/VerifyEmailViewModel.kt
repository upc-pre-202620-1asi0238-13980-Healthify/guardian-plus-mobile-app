package com.example.guardian_plus_mobile_app.features.auth.presentation.verifyemail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.auth.application.ResendEmailVerificationUseCase
import com.example.guardian_plus_mobile_app.features.auth.application.VerifyEmailUseCase
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AUTH_CODE_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val verifyEmail: VerifyEmailUseCase,
    private val resendEmailVerification: ResendEmailVerificationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyEmailUiState())
    val uiState: StateFlow<VerifyEmailUiState> = _uiState.asStateFlow()

    fun onCodeChange(code: String) {
        _uiState.update { it.copy(code = code, errorMessage = null) }
    }

    fun verify(userAccountId: String) {
        val state = _uiState.value
        if (state.isLoading) return
        if (state.code.length != AUTH_CODE_LENGTH) {
            _uiState.update { it.copy(errorMessage = "Ingresa los $AUTH_CODE_LENGTH dígitos del código") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            verifyEmail(userAccountId, state.code)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isVerified = true) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo verificar el correo") }
                }
        }
    }

    fun resend(userAccountId: String, sentMessage: String) {
        if (_uiState.value.isResending) return
        viewModelScope.launch {
            _uiState.update { it.copy(isResending = true) }
            resendEmailVerification(userAccountId)
                .onSuccess { _uiState.update { it.copy(isResending = false, code = "", actionMessage = sentMessage) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isResending = false, actionMessage = e.message ?: "No se pudo reenviar el código") }
                }
        }
    }

    fun onActionMessageShown() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isVerified = false) }
    }
}
