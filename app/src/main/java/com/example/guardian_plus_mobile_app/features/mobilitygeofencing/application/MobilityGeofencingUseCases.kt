package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CurrentLocation
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.SafeZone
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.repository.MobilityGeofencingRepository
import javax.inject.Inject

class MobilityGeofencingUseCases @Inject constructor(
    private val repository: MobilityGeofencingRepository
) {
    suspend fun fetchCurrentTracking(fragileCitizenId: String): Result<CurrentLocation> {
        return repository.getCurrentLocation(fragileCitizenId)
    }

    suspend fun fetchActiveSafeZone(fragileCitizenId: String): Result<SafeZone> {
        return repository.getActiveSafeZone(fragileCitizenId)
    }

    suspend fun toggleSafeZone(safeZoneId: String, isCurrentlyActive: Boolean): Result<Unit> {
        return if (isCurrentlyActive) {
            repository.deactivateSafeZone(safeZoneId)
        } else {
            repository.activateSafeZone(safeZoneId)
        }
    }
}