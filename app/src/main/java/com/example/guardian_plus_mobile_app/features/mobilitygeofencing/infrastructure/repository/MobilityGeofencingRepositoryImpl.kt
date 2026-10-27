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
        if (!response.isSuccessful) {
            throw Exception("No se encontró telemetría de ubicación para este usuario.")
        }
        val dto = response.body() ?: throw Exception("Respuesta vacía del servidor.")
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
        if (!response.isSuccessful) throw Exception("Error al consultar estado.")
        response.body()?.status ?: "UNKNOWN"
    }

    override suspend fun getLocationHistory(
        fragileCitizenId: String,
        start: String?,
        end: String?
    ): Result<List<LocationHistoryItem>> = runCatching {
        val response = api.getLocationHistory(fragileCitizenId, start, end)
        if (!response.isSuccessful) {
            return@runCatching emptyList()
        }
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

    override suspend fun getSafeZonesByCitizen(fragileCitizenId: String): Result<List<SafeZone>> = runCatching {
        val response = api.getSafeZonesByCitizen(fragileCitizenId)
        if (!response.isSuccessful) {
            val activeResponse = api.getActiveSafeZone(fragileCitizenId)
            if (activeResponse.isSuccessful && activeResponse.body() != null) {
                val dto = activeResponse.body()!!
                return@runCatching listOf(
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
            }
            return@runCatching emptyList()
        }
        val list = response.body() ?: emptyList()
        list.map { dto ->
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
    }

    override suspend fun getActiveSafeZone(fragileCitizenId: String): Result<SafeZone> = runCatching {
        val response = api.getActiveSafeZone(fragileCitizenId)
        if (!response.isSuccessful) {
            throw Exception("No se encontró zona segura activa.")
        }
        val dto = response.body() ?: throw Exception("Respuesta vacía del servidor.")
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
        val request = CreateSafeZoneRequest(
            fragileCitizenId = fragileCitizenId,
            name = name,
            centerLatitude = latitude,
            centerLongitude = longitude,
            radiusInMeters = radiusInMeters
        )
        val response = api.createSafeZone(request)
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string() ?: "Error al crear la zona segura en el servidor"
            throw Exception(errorMsg)
        }
        response.body()?.string() ?: "Creado exitosamente"
    }

    override suspend fun updateSafeZone(
        safeZoneId: String,
        name: String,
        latitude: Double,
        longitude: Double,
        radiusInMeters: Double
    ): Result<Unit> = runCatching {
        val request = UpdateSafeZoneRequest(name, latitude, longitude, radiusInMeters)
        val response = api.updateSafeZone(safeZoneId, request)
        if (!response.isSuccessful) throw Exception("Error al actualizar la geocerca")
        Unit
    }

    override suspend fun activateSafeZone(safeZoneId: String): Result<Unit> = runCatching {
        val response = api.activateSafeZone(safeZoneId)
        if (!response.isSuccessful) throw Exception("Error al activar geocerca")
        Unit
    }

    override suspend fun deactivateSafeZone(safeZoneId: String): Result<Unit> = runCatching {
        val response = api.deactivateSafeZone(safeZoneId)
        if (!response.isSuccessful) throw Exception("Error al desactivar geocerca")
        Unit
    }

    override suspend fun deleteSafeZone(safeZoneId: String): Result<Unit> = runCatching {
        val response = api.deleteSafeZone(safeZoneId)
        if (!response.isSuccessful) throw Exception("Error al eliminar geocerca")
        Unit
    }
}