package com.example.guardian_plus_mobile_app.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.SignUpNameStore
import com.example.guardian_plus_mobile_app.features.auth.application.GetEmailAvailabilityUseCase
import com.example.guardian_plus_mobile_app.features.auth.application.RegisterUserCredentialsUseCase
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.MIN_PASSWORD_LENGTH
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.parseEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val getEmailAvailability: GetEmailAvailabilityUseCase,
    private val registerUserCredentials: RegisterUserCredentialsUseCase,
    private val signUpNameStore: SignUpNameStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChange(fullName: String) {
        _uiState.update { it.copy(fullName = fullName, errorMessage = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordHidden = !it.isPasswordHidden) }
    }

    fun register() {
        val state = _uiState.value
        if (state.isLoading) return
        val email = parseEmail(state.email)
        val error = when {
            listOf(state.fullName, state.email, state.password, state.confirmPassword).any { it.isBlank() } ->
                "Completa todos los campos para continuar"
            email == null -> "Ingresa un correo electrónico válido"
            state.password.length < MIN_PASSWORD_LENGTH ->
                "La contraseña debe tener al menos $MIN_PASSWORD_LENGTH caracteres"
            state.password != state.confirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }
        if (error != null || email == null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // IAM keeps one account per email; asking first gives a clear message before sending the password
            val available = getEmailAvailability(email).getOrDefault(true)
            if (!available) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Ya existe una cuenta con este correo") }
                return@launch
            }
            registerUserCredentials(state.fullName.trim(), email, state.password)
                .onSuccess { account ->
                    // Profile is created at the onboarding after the first sign-in, with this same name
                    signUpNameStore.save(account.id, state.fullName.trim())
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            password = "",
                            confirmPassword = "",
                            registeredAccount = RegisteredAccount(account.id, account.email.value)
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo crear la cuenta") }
                }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(registeredAccount = null) }
    }
}
