package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.VerifyDeviceCapabilitiesUseCase
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.DeviceCapabilities
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.DeviceCommunicationCapability
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.LiveVitalsSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeviceStatusCheckUiState(
    val isLoading: Boolean = false,
    val capabilities: DeviceCapabilities = DeviceCapabilities(
        deviceId = "#GC-2847",
        modelName = "Guardian Core",
        citizenName = "Elena",
        citizenCode = "ID #GC-2847",
        capability = DeviceCommunicationCapability.TELEMETRY_ONLY,
        hasCamera = false,
        hasBidirectionalAudio = false,
        hasSpeaker = false,
        hasMicrophone = false,
        hasHapticAlerts = true,
        hasTelemetry = true,
        citizenPhoneNumber = "+51 987 654 321",
        caregiverName = "Rosa Gómez",
        caregiverRole = "Cuidadora",
        caregiverPhoneNumber = "+51 912 345 678"
    ),
    val lastVitals: LiveVitalsSnapshot = LiveVitalsSnapshot(
        bpm = 76,
        spo2Percentage = 97,
        temperatureCelsius = 36.7
    )
)

@HiltViewModel
class DeviceStatusCheckViewModel @Inject constructor(
    private val verifyDeviceCapabilitiesUseCase: VerifyDeviceCapabilitiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceStatusCheckUiState())
    val uiState: StateFlow<DeviceStatusCheckUiState> = _uiState.asStateFlow()

    fun loadDeviceStatus(fragileCitizenId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            verifyDeviceCapabilitiesUseCase(
                fragileCitizenId = fragileCitizenId,
                simulatedCapability = DeviceCommunicationCapability.TELEMETRY_ONLY
            ).onSuccess { capabilities ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        capabilities = capabilities
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
