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
                .onFailure {
                    repository.getActiveSafeZone(fragileCitizenId)
                        .onSuccess { activeZone ->
                            _uiState.value = SafeZonesUiState.Success(listOf(activeZone))
                        }
                        .onFailure {
                            _uiState.value = SafeZonesUiState.Success(emptyList())
                        }
                }
        }
    }
    fun deleteSafeZone(safeZoneId: String, fragileCitizenId: String) {
        viewModelScope.launch {
            repository.deleteSafeZone(safeZoneId)
                .onSuccess {
                    _snackbarMessage.value = "Zona segura eliminada con éxito"
                    loadSafeZones(fragileCitizenId)
                }
                .onFailure {
                    _snackbarMessage.value = "Error al eliminar la zona segura"
                }
        }
    }

    fun updateSafeZone(safeZoneId: String, name: String, latitude: Double, longitude: Double, radius: Double, fragileCitizenId: String) {
        viewModelScope.launch {
            repository.updateSafeZone(safeZoneId, name, latitude, longitude, radius)
                .onSuccess {
                    _snackbarMessage.value = "Zona segura actualizada con éxito"
                    loadSafeZones(fragileCitizenId)
                }
                .onFailure {
                    _snackbarMessage.value = "Error al actualizar la zona"
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