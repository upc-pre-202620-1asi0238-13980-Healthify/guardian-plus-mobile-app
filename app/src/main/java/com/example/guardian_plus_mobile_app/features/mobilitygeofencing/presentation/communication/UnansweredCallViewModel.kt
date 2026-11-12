package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.ManageCallTimeoutUseCase
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallLogEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UnansweredCallUiState(
    val isLoading: Boolean = false,
    val citizenName: String = "Elena Martínez",
    val missedAtTime: String = "10:42 AM",
    val timeoutSeconds: Int = ManageCallTimeoutUseCase.CALL_TIMEOUT_SECONDS,
    val isRegisteredInLog: Boolean = true,
    val hapticFeedbackMessage: String? = null,
    val callHistory: List<CallLogEntry> = listOf(
        CallLogEntry(
            id = "log-1",
            fragileCitizenId = "default",
            title = "Videollamada perdida",
            timestamp = "10:42:00 AM",
            statusLabel = "Sin respuesta",
            isMissed = true
        ),
        CallLogEntry(
            id = "log-2",
            fragileCitizenId = "default",
            title = "Llamada saliente",
            timestamp = "10:41:30 AM",
            statusLabel = "Iniciada",
            isMissed = false
        )
    )
)

@HiltViewModel
class UnansweredCallViewModel @Inject constructor(
    private val manageCallTimeoutUseCase: ManageCallTimeoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UnansweredCallUiState())
    val uiState: StateFlow<UnansweredCallUiState> = _uiState.asStateFlow()

    fun registerAndLoadLogs(fragileCitizenId: String, callId: String = "call-2847") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            manageCallTimeoutUseCase.registerTimeoutInLog(
                fragileCitizenId = fragileCitizenId,
                callId = callId,
                waitDurationSeconds = ManageCallTimeoutUseCase.CALL_TIMEOUT_SECONDS
            ).onSuccess { logs ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRegisteredInLog = true,
                        callHistory = logs
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun sendHapticAlert(fragileCitizenId: String) {
        viewModelScope.launch {
            manageCallTimeoutUseCase.sendHapticBuzzAlert(fragileCitizenId).onSuccess {
                _uiState.update {
                    it.copy(hapticFeedbackMessage = "Alerta vibratoria (zumbido) enviada a la pulsera")
                }
            }
        }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(hapticFeedbackMessage = null) }
    }
}
