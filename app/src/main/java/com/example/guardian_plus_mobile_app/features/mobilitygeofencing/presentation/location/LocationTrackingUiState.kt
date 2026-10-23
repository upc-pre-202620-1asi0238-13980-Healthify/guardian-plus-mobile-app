package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CurrentLocation

sealed interface LocationTrackingUiState {
    object Loading : LocationTrackingUiState
    data class Success(
        val location: CurrentLocation,
        val isWithinSafeZone: Boolean
    ) : LocationTrackingUiState
    data class Error(val message: String) : LocationTrackingUiState
}