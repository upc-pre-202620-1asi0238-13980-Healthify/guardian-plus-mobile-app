package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.wearable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.LinkWearableDeviceUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.DeviceType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LinkWearableUiState(
    val serialNumber: String = "",
    val deviceType: DeviceType = DeviceType.WRISTBAND,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Set once the platform linked the device; the sheet closes and clears it
    val isLinked: Boolean = false
)

/** Links a wearable to the care recipient of the signed-in account. */
@HiltViewModel
class LinkWearableViewModel @Inject constructor(
    private val linkWearableDevice: LinkWearableDeviceUseCase,
    private val careContextStore: CareContextStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(LinkWearableUiState())
    val uiState: StateFlow<LinkWearableUiState> = _uiState.asStateFlow()

    fun onSerialNumberChange(value: String) = _uiState.update { it.copy(serialNumber = value, errorMessage = null) }

    fun onDeviceTypeChange(value: DeviceType) = _uiState.update { it.copy(deviceType = value) }

    fun link() {
        val state = _uiState.value
        if (state.isLoading) return
        if (state.serialNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa el número de serie del dispositivo") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // A serial number already linked (409) comes back with the platform's own message
            linkWearableDevice(careContextStore.require().careRecipientProfileId, state.serialNumber, state.deviceType)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isLinked = true) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo vincular el dispositivo") }
                }
        }
    }

    /** Leaves the form empty for the next time the sheet opens. */
    fun reset() = _uiState.update { LinkWearableUiState() }
}
