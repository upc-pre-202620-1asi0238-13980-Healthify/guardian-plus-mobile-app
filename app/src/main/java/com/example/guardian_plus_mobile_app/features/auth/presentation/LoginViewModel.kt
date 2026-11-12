package com.example.guardian_plus_mobile_app.features.auth.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.auth.application.LoginUseCase
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.ContinueWithGoogle
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.googleSignInErrorMessage
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.parseEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val continueWithGoogle: ContinueWithGoogle
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordHidden = !it.isPasswordHidden) }
    }

    fun login() {
        val state = _uiState.value
        if (state.isLoading) return
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Completa todos los campos para continuar") }
            return
        }
        val email = parseEmail(state.email)
        if (email == null) {
            _uiState.update { it.copy(errorMessage = "Ingresa un correo electrónico válido") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            login(email, state.password)
                .onSuccess { outcome ->
                    val navigation = when (outcome) {
                        is LoginOutcome.SecondFactorRequired -> LoginNavigation.TwoFactor(outcome.userAccountId, email.value)
                        is LoginOutcome.EmailVerificationRequired ->
                            LoginNavigation.VerifyEmail(outcome.userAccountId, email.value)
                    }
                    // The password is not kept once it has served its purpose
                    _uiState.update { it.copy(isLoading = false, password = "", navigation = navigation) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo iniciar sesión") }
                }
        }
    }

    /** [activityContext] hosts Google's account picker; it is only used during the call, never kept. */
    fun signInWithGoogle(activityContext: Context) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            continueWithGoogle(activityContext)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, navigation = LoginNavigation.SignedIn) }
                }
                .onFailure { e ->
                    // Closing the account picker just leaves the login as it was
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.googleSignInErrorMessage()) }
                }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(navigation = null) }
    }
}
