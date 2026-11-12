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

data class VoiceCallUiState(
    val isLoading: Boolean = false,
    val callId: String = "",
    val citizenName: String = "Elena Martínez",
    val initials: String = "EM",
    val deviceModel: String = "Guardian Band Lite",
    val deviceId: String = "ID #PB-2847",
    val elapsedSeconds: Int = 86,
    val isMicMuted: Boolean = false,
    val isSpeakerEnabled: Boolean = true,
    val waveformBars: List<Float> = listOf(
        0.25f, 0.45f, 0.30f, 0.70f, 0.95f, 0.50f, 0.35f,
        0.80f, 0.60f, 0.30f, 0.55f, 0.90f, 0.65f, 0.40f, 0.25f
    ),
    val vitals: LiveVitalsSnapshot = LiveVitalsSnapshot(
        bpm = 76,
        spo2Percentage = 97
    ),
    val errorMessage: String? = null
)

@HiltViewModel
class VoiceCallViewModel @Inject constructor(
    private val initiateDirectCommunicationUseCase: InitiateDirectCommunicationUseCase,
    private val manageCallTimeoutUseCase: ManageCallTimeoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceCallUiState())
    val uiState: StateFlow<VoiceCallUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var waveformJob: Job? = null

    fun startVoiceCall(fragileCitizenId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            initiateDirectCommunicationUseCase(fragileCitizenId)
                .onSuccess { result ->
                    when (result) {
                        is DirectCommunicationRoutingResult.FallbackToVoiceCall -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    callId = result.session.callId,
                                    citizenName = result.session.citizenName.ifBlank { it.citizenName },
                                    deviceModel = result.capabilities.modelName.ifBlank { it.deviceModel },
                                    deviceId = result.capabilities.citizenCode.ifBlank { it.deviceId },
                                    vitals = result.session.vitals
                                )
                            }
                        }
                        is DirectCommunicationRoutingResult.StartVideoCall -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    callId = result.session.callId,
                                    citizenName = result.session.citizenName.ifBlank { it.citizenName },
                                    vitals = result.session.vitals
                                )
                            }
                        }
                        else -> _uiState.update { it.copy(isLoading = false) }
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

            startTimers()
        }
    }

    private fun startTimers() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }

        waveformJob?.cancel()
        waveformJob = viewModelScope.launch {
            while (true) {
                delay(350L)
                _uiState.update { state ->
                    if (state.isMicMuted) {
                        state
                    } else {
                        val rotated = state.waveformBars.drop(1) + state.waveformBars.first()
                        state.copy(waveformBars = rotated)
                    }
                }
            }
        }
    }

    fun toggleMic() {
        _uiState.update { it.copy(isMicMuted = !it.isMicMuted) }
    }

    fun toggleSpeaker() {
        _uiState.update { it.copy(isSpeakerEnabled = !it.isSpeakerEnabled) }
    }

    fun endCall(onEnded: () -> Unit) {
        viewModelScope.launch {
            timerJob?.cancel()
            waveformJob?.cancel()
            if (_uiState.value.callId.isNotBlank()) {
                manageCallTimeoutUseCase.endActiveCall(_uiState.value.callId)
            }
            onEnded()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        waveformJob?.cancel()
    }
}
