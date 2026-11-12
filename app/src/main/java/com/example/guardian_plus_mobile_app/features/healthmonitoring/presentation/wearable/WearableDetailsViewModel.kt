package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.wearable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetWearableDevicesUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.UnlinkWearableDeviceUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.DeviceType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WearableDetailsViewModel @Inject constructor(
    private val getWearableDevices: GetWearableDevicesUseCase,
    private val unlinkWearableDevice: UnlinkWearableDeviceUseCase,
    private val careContextStore: CareContextStore
) : ViewModel() {

    private val careContext = careContextStore.require()

    private val _uiState = MutableStateFlow(
        WearableDetailsUiState(
            careRecipientName = careContext.careRecipientName
        )
    )

    val uiState: StateFlow<WearableDetailsUiState> =
        _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            getWearableDevices(
                careContext.careRecipientProfileId
            )
                .onSuccess { devices ->

                    val wearable =
                        devices.firstOrNull {
                            it.deviceType == DeviceType.WRISTBAND
                        } ?: devices.firstOrNull()

                    _uiState.update {
                        it.copy(
                            wearable = wearable,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                "No se pudo cargar el dispositivo"
                        )
                    }
                }
        }
    }

    /** "Desvincular pulsera", once confirmed. Another device of the care recipient takes its place, if any. */
    fun unlink() {
        val wearable = _uiState.value.wearable ?: return
        if (_uiState.value.isUnlinking) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUnlinking = true) }
            unlinkWearableDevice(wearable.id)
                .onSuccess {
                    _uiState.update { it.copy(isUnlinking = false, actionMessage = "Pulsera desvinculada") }
                    load()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isUnlinking = false, actionMessage = e.message ?: "No se pudo desvincular la pulsera")
                    }
                }
        }
    }

    fun onActionMessageShown() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
