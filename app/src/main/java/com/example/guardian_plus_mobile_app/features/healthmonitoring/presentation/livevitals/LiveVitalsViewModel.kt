package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetLiveVitalSignsUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetWearableDevicesUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.DeviceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.LiveVitalsUiState

import javax.inject.Inject


@HiltViewModel
class LiveVitalsViewModel @Inject constructor(
    private val getLiveVitalSigns: GetLiveVitalSignsUseCase,
    private val getWearableDevices: GetWearableDevicesUseCase,
    private val serverClock: ServerClock
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        LiveVitalsUiState(careRecipientFirstName = DemoSession.CARE_RECIPIENT_FIRST_NAME)
    )

    val uiState: StateFlow<LiveVitalsUiState> = _uiState.asStateFlow()

    init {
        load()
        startAutoRefresh()
    }


    fun load() {
        viewModelScope.launch{
            _uiState.update { it.copy(isLoading = true, errorMessage = null)}
            refresh()
        }

    }



        private suspend fun refresh() {
            val hasWristband = getWearableDevices(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .getOrDefault(emptyList())
                .any { it.deviceType == DeviceType.WRISTBAND}

                getLiveVitalSigns(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .onSuccess {  vitals ->
                    //this is basically updating the states that we previously defined
                    _uiState.update { 
                        it.copy(isLoading = false, errorMessage = null, vitals = vitals, hasWristband = hasWristband, now = serverClock.now())
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, hasWristband = hasWristband, now = serverClock.now(), errorMessage = e.message?: "No se pudieron cargar los signos vitales")
                    }
                }
                
        }

        private fun startAutoRefresh() {
            viewModelScope.launch { 
                while (true) {
                    delay(AUTO_REFRESH_MS)
                    if (!_uiState.value.isLoading){
                        refresh()
                    }
                }

            }
        }

        private companion object {
            const val AUTO_REFRESH_MS = 10_000L
        }
}
