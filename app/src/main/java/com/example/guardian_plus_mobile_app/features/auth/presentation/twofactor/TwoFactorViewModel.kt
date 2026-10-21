package com.example.guardian_plus_mobile_app.features.auth.presentation.twofactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.auth.application.VerifyOtpUseCase
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AUTH_CODE_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TwoFactorViewModel @Inject constructor(
    private val verifyOtp: VerifyOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TwoFactorUiState())
    val uiState: StateFlow<TwoFactorUiState> = _uiState.asStateFlow()

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
            verifyOtp(userAccountId, state.code)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo verificar el código") }
                }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isAuthenticated = false) }
    }
}
