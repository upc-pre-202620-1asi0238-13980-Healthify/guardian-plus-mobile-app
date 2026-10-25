package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.repository

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CurrentLocation
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.LocationHistoryItem
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.SafeZone
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.repository.MobilityGeofencingRepository
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.MobilityGeofencingApi
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto.CreateSafeZoneRequest
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto.UpdateSafeZoneRequest
import javax.inject.Inject

class MobilityGeofencingRepositoryImpl @Inject constructor(
    private val api: MobilityGeofencingApi
) : MobilityGeofencingRepository {

    override suspend fun getCurrentLocation(fragileCitizenId: String): Result<CurrentLocation> = runCatching {
        val response = api.getCurrentLocation(fragileCitizenId)
        val dto = response.body() ?: throw Exception("Error al obtener la ubicación actual")
        CurrentLocation(
            fragileCitizenId = dto.fragileCitizenId,
            latitude = dto.latitude ?: 0.0,
            longitude = dto.longitude ?: 0.0,
            accuracyInMeters = dto.accuracyInMeters ?: 0.0,
            status = dto.status ?: "UNKNOWN",
            recordedAt = dto.recordedAt ?: ""
        )
    }

    override suspend fun getCurrentStatus(fragileCitizenId: String): Result<String> = runCatching {
        val response = api.getCurrentStatus(fragileCitizenId)
        response.body()?.string() ?: "UNKNOWN"
    }

    override suspend fun getLocationHistory(
        fragileCitizenId: String,
        start: String?,
        end: String?
    ): Result<List<LocationHistoryItem>> = runCatching {
        val response = api.getLocationHistory(fragileCitizenId, start, end)
        val dtoList = response.body() ?: emptyList()
        dtoList.map { dto ->
            LocationHistoryItem(
                latitude = dto.latitude,
                longitude = dto.longitude,
                accuracyInMeters = dto.accuracyInMeters,
                status = dto.status,
                recordedAt = dto.recordedAt
            )
        }
    }

    override suspend fun getActiveSafeZone(fragileCitizenId: String): Result<SafeZone> = runCatching {
        val response = api.getActiveSafeZone(fragileCitizenId)
        val dto = response.body() ?: throw Exception("Sin zona segura activa")
        SafeZone(
            id = dto.id,
            fragileCitizenId = dto.fragileCitizenId,
            name = dto.name,
            latitude = dto.latitude,
            longitude = dto.longitude,
            radiusInMeters = dto.radiusInMeters,
            status = dto.status
        )
    }

    override suspend fun createSafeZone(
        fragileCitizenId: String,
        name: String,
        latitude: Double,
        longitude: Double,
        radiusInMeters: Double
    ): Result<String> = runCatching {
        val request = CreateSafeZoneRequest(fragileCitizenId, name, latitude, longitude, radiusInMeters)
        val response = api.createSafeZone(request)
        response.body() ?: throw Exception("Error al crear la zona segura")
    }

    override suspend fun updateSafeZone(
        safeZoneId: String,
        name: String,
        latitude: Double,
        longitude: Double,
        radiusInMeters: Double
    ): Result<Unit> = runCatching {
        val request = UpdateSafeZoneRequest(name, latitude, longitude, radiusInMeters)
        api.updateSafeZone(safeZoneId, request)
        Unit
    }

    override suspend fun activateSafeZone(safeZoneId: String): Result<Unit> = runCatching {
        api.activateSafeZone(safeZoneId)
        Unit
    }

    override suspend fun deactivateSafeZone(safeZoneId: String): Result<Unit> = runCatching {
        api.deactivateSafeZone(safeZoneId)
        Unit
    }

    override suspend fun deleteSafeZone(safeZoneId: String): Result<Unit> = runCatching {
        api.deleteSafeZone(safeZoneId)
        Unit
    }
}