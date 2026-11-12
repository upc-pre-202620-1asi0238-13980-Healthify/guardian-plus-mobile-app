package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.InitiateDirectCommunicationUseCase
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.ManageCallTimeoutUseCase
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.DeviceCommunicationCapability
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
    val callId: String = "call-video-2847",
    val citizenName: String = "Elena Martínez",
    val citizenRoleAndCode: String = "Ciudadana Frágil • ID #2847",
    val deviceModel: String = "Guardian Pro",
    val elapsedSeconds: Int = 167, // Starts at 02:47 to match design and ticks up
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
    val timedOut: Boolean = false
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
            _uiState.update { it.copy(isLoading = true, timedOut = false) }
            initiateDirectCommunicationUseCase(
                fragileCitizenId = fragileCitizenId,
                simulatedCapability = DeviceCommunicationCapability.VIDEO_AND_AUDIO
            ).onSuccess { result ->
                if (result is com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.DirectCommunicationRoutingResult.StartVideoCall) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            callId = result.session.callId,
                            citizenName = result.session.citizenName,
                            citizenRoleAndCode = "Ciudadana Frágil • ${result.session.citizenCode}",
                            deviceModel = result.session.deviceModel,
                            vitals = result.session.vitals,
                            isConnecting = simulateRingingTimeout
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
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
                    val jitterBpm = if (nextSeconds % 5 == 0) 77 else 76
                    state.copy(
                        elapsedSeconds = nextSeconds,
                        vitals = state.vitals.copy(bpm = jitterBpm)
                    )
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
            manageCallTimeoutUseCase.endActiveCall(_uiState.value.callId)
            onEnded()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        timeoutJob?.cancel()
    }
}
