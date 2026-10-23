package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.SafeZone

sealed interface SafeZonesUiState {
    object Loading : SafeZonesUiState
    data class Success(val activeZone: SafeZone?) : SafeZonesUiState
    data class Error(val message: String) : SafeZonesUiState
}