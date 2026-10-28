package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.MobilityGeofencingUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationTrackingViewModel @Inject constructor(
    private val useCases: MobilityGeofencingUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<LocationTrackingUiState>(LocationTrackingUiState.Loading)
    val uiState: StateFlow<LocationTrackingUiState> = _uiState.asStateFlow()

    fun loadCurrentLocation(fragileCitizenId: String) {
        viewModelScope.launch {
            _uiState.value = LocationTrackingUiState.Loading
            // The active zone is what the map draws around; a person may have none
            val activeSafeZone = async { useCases.fetchActiveSafeZone(fragileCitizenId).getOrNull() }
            useCases.fetchCurrentTracking(fragileCitizenId)
                .onSuccess { location ->
                    _uiState.value = LocationTrackingUiState.Success(location, activeSafeZone.await())
                }
                .onFailure { error ->
                    _uiState.value = LocationTrackingUiState.Error(error.localizedMessage ?: "Error al cargar ubicación")
                }
        }
    }
}
