package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.DirectCommunicationRoutingResult
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.InitiateDirectCommunicationUseCase
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.ManageCallTimeoutUseCase
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.LiveVitalsSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideoCallUiState(
    val isLoading: Boolean = false,
    val callId: String = "",
    val citizenName: String = "Elena Martínez",
    val citizenRoleAndCode: String = "Ciudadana Frágil • ID #2847",
    val deviceModel: String = "Guardian Pro",
    val elapsedSeconds: Int = 167,
    val isConnecting: Boolean = false,
    val unansweredWaitSeconds: Int = 0,
    val isMicMuted: Boolean = false,
    val isCameraEnabled: Boolean = true,
    val isSpeakerEnabled: Boolean = true,
    val vitals: LiveVitalsSnapshot = LiveVitalsSnapshot(
        bpm = 76,
        spo2Percentage = 97,
        temperatureCelsius = 36.7,
        bloodPressure = "120/80"
    ),
    val timedOut: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class VideoCallViewModel @Inject constructor(
    private val initiateDirectCommunicationUseCase: InitiateDirectCommunicationUseCase,
    private val manageCallTimeoutUseCase: ManageCallTimeoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoCallUiState())
    val uiState: StateFlow<VideoCallUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var timeoutJob: Job? = null

    fun startVideoCall(fragileCitizenId: String, simulateRingingTimeout: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, timedOut = false, errorMessage = null) }
            initiateDirectCommunicationUseCase(fragileCitizenId)
                .onSuccess { result ->
                    if (result is DirectCommunicationRoutingResult.StartVideoCall) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                callId = result.session.callId,
                                citizenName = result.session.citizenName.ifBlank { it.citizenName },
                                citizenRoleAndCode = "Ciudadana Frágil • ${result.session.citizenCode.ifBlank { "ID #2847" }}",
                                deviceModel = result.session.deviceModel.ifBlank { it.deviceModel },
                                vitals = result.session.vitals,
                                isConnecting = simulateRingingTimeout
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }

            if (simulateRingingTimeout) {
                startThirtySecondTimeoutCountdown(fragileCitizenId)
            } else {
                startCallDurationTimer()
            }
        }
    }

    private fun startCallDurationTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _uiState.update { state ->
                    val nextSeconds = state.elapsedSeconds + 1
                    state.copy(elapsedSeconds = nextSeconds)
                }
            }
        }
    }

    fun startThirtySecondTimeoutCountdown(fragileCitizenId: String) {
        timeoutJob?.cancel()
        timeoutJob = viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true, unansweredWaitSeconds = 0) }
            for (sec in 1..ManageCallTimeoutUseCase.CALL_TIMEOUT_SECONDS) {
                delay(1000L)
                _uiState.update { it.copy(unansweredWaitSeconds = sec) }
            }
            manageCallTimeoutUseCase.registerTimeoutInLog(
                fragileCitizenId = fragileCitizenId,
                callId = _uiState.value.callId,
                waitDurationSeconds = ManageCallTimeoutUseCase.CALL_TIMEOUT_SECONDS
            )
            _uiState.update { it.copy(isConnecting = false, timedOut = true) }
        }
    }

    fun toggleMic() {
        _uiState.update { it.copy(isMicMuted = !it.isMicMuted) }
    }

    fun toggleCamera() {
        _uiState.update { it.copy(isCameraEnabled = !it.isCameraEnabled) }
    }

    fun toggleSpeaker() {
        _uiState.update { it.copy(isSpeakerEnabled = !it.isSpeakerEnabled) }
    }

    fun endCall(onEnded: () -> Unit) {
        viewModelScope.launch {
            timerJob?.cancel()
            timeoutJob?.cancel()
            if (_uiState.value.callId.isNotBlank()) {
                manageCallTimeoutUseCase.endActiveCall(_uiState.value.callId)
            }
            onEnded()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        timeoutJob?.cancel()
    }
}
