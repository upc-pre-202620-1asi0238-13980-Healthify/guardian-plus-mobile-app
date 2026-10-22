package com.example.guardian_plus_mobile_app.features.auth.presentation.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.auth.application.ResendEmailVerificationUseCase
import com.example.guardian_plus_mobile_app.features.auth.application.ResendOtpUseCase
import com.example.guardian_plus_mobile_app.features.auth.application.VerifyEmailUseCase
import com.example.guardian_plus_mobile_app.features.auth.application.VerifyOtpUseCase
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.OtpPurpose
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Checks a code IAM emailed. The purpose decides which one: EMAIL_VERIFICATION activates a new account,
 * LOGIN completes the sign-in of an account with two-factor enabled.
 */
@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val verifyEmail: VerifyEmailUseCase,
    private val verifyOtp: VerifyOtpUseCase,
    private val resendEmailVerification: ResendEmailVerificationUseCase,
    private val resendOtp: ResendOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpVerificationUiState())
    val uiState: StateFlow<OtpVerificationUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        // A code has just been sent, so a new one can only be asked for after the cooldown
        startResendCountdown()
    }

    fun onCodeChange(code: String) {
        _uiState.update { it.copy(code = code, errorMessage = null) }
    }

    fun verify(userAccountId: String, purpose: OtpPurpose) {
        val state = _uiState.value
        if (state.isLoading || !state.isComplete) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = when (purpose) {
                OtpPurpose.EMAIL_VERIFICATION -> verifyEmail(userAccountId, state.code).map { }
                OtpPurpose.LOGIN -> verifyOtp(userAccountId, state.code).map { }
            }
            result
                .onSuccess { _uiState.update { it.copy(isLoading = false, isVerified = true) } }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, code = "", errorMessage = e.message ?: "No se pudo verificar el código")
                    }
                }
        }
    }

    fun resend(userAccountId: String, purpose: OtpPurpose, sentMessage: String) {
        if (!_uiState.value.canResend) return
        viewModelScope.launch {
            _uiState.update { it.copy(isResending = true) }
            val result = when (purpose) {
                OtpPurpose.EMAIL_VERIFICATION -> resendEmailVerification(userAccountId)
                OtpPurpose.LOGIN -> resendOtp(userAccountId)
            }
            result
                .onSuccess {
                    _uiState.update { it.copy(isResending = false, code = "", errorMessage = null, actionMessage = sentMessage) }
                    startResendCountdown()
                }
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

    private fun startResendCountdown() {
        countdownJob?.cancel()
        _uiState.update { it.copy(resendAvailableInSec = RESEND_COOLDOWN_SEC) }
        countdownJob = viewModelScope.launch {
            while (_uiState.value.resendAvailableInSec > 0) {
                delay(1_000)
                _uiState.update { it.copy(resendAvailableInSec = it.resendAvailableInSec - 1) }
            }
        }
    }
}
