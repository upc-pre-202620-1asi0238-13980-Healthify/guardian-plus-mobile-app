package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.SafeZone
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.repository.MobilityGeofencingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SafeZoneViewModel @Inject constructor(
    private val repository: MobilityGeofencingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SafeZonesUiState>(SafeZonesUiState.Loading)
    val uiState: StateFlow<SafeZonesUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun loadSafeZones(fragileCitizenId: String) {
        viewModelScope.launch {
            _uiState.value = SafeZonesUiState.Loading
            repository.getSafeZonesByCitizen(fragileCitizenId)
                .onSuccess { zones ->
                    _uiState.value = SafeZonesUiState.Success(zones)
                }
                .onFailure { error ->
                    _uiState.value = SafeZonesUiState.Success(emptyList())
                }
        }
    }

    fun toggleSafeZoneStatus(safeZoneId: String, currentActive: Boolean, fragileCitizenId: String) {
        viewModelScope.launch {
            val result = if (currentActive) {
                repository.deactivateSafeZone(safeZoneId)
            } else {
                repository.activateSafeZone(safeZoneId)
            }
            result.onSuccess {
                _snackbarMessage.value = "Estado de zona actualizado con éxito"
                loadSafeZones(fragileCitizenId)
            }
        }
    }

    fun clearMessage() {
        _snackbarMessage.value = null
    }
}