package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.MobilityGeofencingUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SafeZoneViewModel @Inject constructor(
    private val useCases: MobilityGeofencingUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<SafeZonesUiState>(SafeZonesUiState.Loading)
    val uiState: StateFlow<SafeZonesUiState> = _uiState.asStateFlow()

    fun loadActiveSafeZone(fragileCitizenId: String) {
        viewModelScope.launch {
            _uiState.value = SafeZonesUiState.Loading
            useCases.fetchActiveSafeZone(fragileCitizenId)
                .onSuccess { zone ->
                    _uiState.value = SafeZonesUiState.Success(zone)
                }
                .onFailure {
                    _uiState.value = SafeZonesUiState.Success(null)
                }
        }
    }

    fun toggleSafeZoneStatus(safeZoneId: String, currentActive: Boolean, fragileCitizenId: String) {
        viewModelScope.launch {
            useCases.toggleSafeZone(safeZoneId, currentActive)
                .onSuccess {
                    loadActiveSafeZone(fragileCitizenId)
                }
        }
    }
}