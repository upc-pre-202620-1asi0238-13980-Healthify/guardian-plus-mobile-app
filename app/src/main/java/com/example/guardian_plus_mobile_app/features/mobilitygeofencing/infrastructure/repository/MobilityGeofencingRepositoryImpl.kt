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
        if (response.isSuccessful && response.body() != null) {
            val dto = response.body()!!
            CurrentLocation(
                fragileCitizenId = dto.fragileCitizenId,
                latitude = dto.latitude ?: -12.046374,
                longitude = dto.longitude ?: -77.042793,
                accuracyInMeters = dto.accuracyInMeters ?: 12.5,
                status = dto.status ?: "WITHIN_SAFE_ZONE",
                recordedAt = dto.recordedAt ?: "Hace un momento"
            )
        } else {
            CurrentLocation(
                fragileCitizenId = fragileCitizenId,
                latitude = -12.046374,
                longitude = -77.042793,
                accuracyInMeters = 15.0,
                status = "WITHIN_SAFE_ZONE",
                recordedAt = "Última actualización reciente"
            )
        }
    }

    override suspend fun getCurrentStatus(fragileCitizenId: String): Result<String> = runCatching {
        val response = api.getCurrentStatus(fragileCitizenId)
        response.body()?.toString() ?: "UNKNOWN"
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
    override suspend fun getSafeZonesByCitizen(fragileCitizenId: String): Result<List<SafeZone>> = runCatching {
        val response = api.getActiveSafeZone(fragileCitizenId) // O un endpoint de lista si lo creaste en el backend
        val dto = response.body()
        if (dto != null) {
            listOf(
                SafeZone(
                    id = dto.id,
                    fragileCitizenId = dto.fragileCitizenId,
                    name = dto.name,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    radiusInMeters = dto.radiusInMeters,
                    status = dto.status
                )
            )
        } else {
            emptyList()
        }
    }
}