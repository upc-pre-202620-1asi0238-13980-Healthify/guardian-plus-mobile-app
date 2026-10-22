package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.repository

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CurrentLocation
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.LocationHistoryItem
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.SafeZone

interface MobilityGeofencingRepository {
    suspend fun getCurrentLocation(fragileCitizenId: String): Result<CurrentLocation>
    suspend fun getCurrentStatus(fragileCitizenId: String): Result<String>
    suspend fun getLocationHistory(fragileCitizenId: String, start: String? = null, end: String? = null): Result<List<LocationHistoryItem>>
    suspend fun getActiveSafeZone(fragileCitizenId: String): Result<SafeZone>
    suspend fun createSafeZone(fragileCitizenId: String, name: String, latitude: Double, longitude: Double, radiusInMeters: Double): Result<String>
    suspend fun updateSafeZone(safeZoneId: String, name: String, latitude: Double, longitude: Double, radiusInMeters: Double): Result<Unit>
    suspend fun activateSafeZone(safeZoneId: String): Result<Unit>
    suspend fun deactivateSafeZone(safeZoneId: String): Result<Unit>
    suspend fun deleteSafeZone(safeZoneId: String): Result<Unit>
}