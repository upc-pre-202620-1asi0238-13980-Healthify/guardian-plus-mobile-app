package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CurrentLocation
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.SafeZone

sealed interface LocationTrackingUiState {
    object Loading : LocationTrackingUiState
    data class Success(
        val location: CurrentLocation,
        // null when the person has no active safe zone: there is nothing to be inside or outside of
        val activeSafeZone: SafeZone?
    ) : LocationTrackingUiState {
        val isWithinSafeZone: Boolean get() = location.status != "OUTSIDE_SAFE_ZONE"
    }
    data class Error(val message: String) : LocationTrackingUiState
}
