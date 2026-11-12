package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.repository

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallLogEntry
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallSession
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallStatus
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallType
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CurrentLocation
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.DeviceCapabilities
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.DeviceCommunicationCapability
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.LiveVitalsSnapshot
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.LocationHistoryItem
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.SafeZone
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.repository.MobilityGeofencingRepository
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.MobilityGeofencingApi
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto.CreateSafeZoneRequest
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto.HapticAlertRequestDto
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto.RegisterCallLogRequestDto
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto.StartCallRequestDto
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto.UpdateSafeZoneRequest
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class MobilityGeofencingRepositoryImpl @Inject constructor(
    private val api: MobilityGeofencingApi
) : MobilityGeofencingRepository {

    private val localCallLogs = mutableListOf(
        CallLogEntry(
            id = "log-1",
            fragileCitizenId = "default",
            title = "Videollamada perdida",
            timestamp = "10:42:00 AM",
            statusLabel = "Sin respuesta",
            isMissed = true
        ),
        CallLogEntry(
            id = "log-2",
            fragileCitizenId = "default",
            title = "Llamada saliente",
            timestamp = "10:41:30 AM",
            statusLabel = "Iniciada",
            isMissed = false
        )
    )

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

    override suspend fun getDeviceCapabilities(
        fragileCitizenId: String,
        simulatedCapability: DeviceCommunicationCapability?
    ): Result<DeviceCapabilities> = runCatching {
        if (simulatedCapability != null) {
            return@runCatching buildFallbackCapabilities(simulatedCapability)
        }
        val response = runCatching { api.getDeviceCapabilities(fragileCitizenId) }.getOrNull()
        val dto = response?.takeIf { it.isSuccessful }?.body()
        if (dto != null) {
            val capability = when (dto.capability) {
                "AUDIO_ONLY" -> DeviceCommunicationCapability.AUDIO_ONLY
                "TELEMETRY_ONLY" -> DeviceCommunicationCapability.TELEMETRY_ONLY
                else -> DeviceCommunicationCapability.VIDEO_AND_AUDIO
            }
            DeviceCapabilities(
                deviceId = dto.deviceId ?: "#2847",
                modelName = dto.modelName ?: "Guardian Pro",
                citizenName = dto.citizenName ?: "Elena Martínez",
                citizenCode = dto.citizenCode ?: "ID #2847",
                capability = capability,
                hasCamera = dto.hasCamera ?: (capability == DeviceCommunicationCapability.VIDEO_AND_AUDIO),
                hasBidirectionalAudio = dto.hasBidirectionalAudio ?: (capability != DeviceCommunicationCapability.TELEMETRY_ONLY),
                hasSpeaker = dto.hasSpeaker ?: (capability != DeviceCommunicationCapability.TELEMETRY_ONLY),
                hasMicrophone = dto.hasMicrophone ?: (capability != DeviceCommunicationCapability.TELEMETRY_ONLY),
                hasHapticAlerts = dto.hasHapticAlerts ?: true,
                hasTelemetry = dto.hasTelemetry ?: true,
                citizenPhoneNumber = dto.citizenPhoneNumber ?: "+51 987 654 321",
                caregiverName = dto.caregiverName ?: "Rosa Gómez",
                caregiverRole = dto.caregiverRole ?: "Cuidadora",
                caregiverPhoneNumber = dto.caregiverPhoneNumber ?: "+51 912 345 678"
            )
        } else {
            buildFallbackCapabilities(DeviceCommunicationCapability.VIDEO_AND_AUDIO)
        }
    }

    override suspend fun startCall(
        fragileCitizenId: String,
        preferredCallType: CallType
    ): Result<CallSession> = runCatching {
        val response = runCatching {
            api.startCall(
                StartCallRequestDto(
                    fragileCitizenId = fragileCitizenId,
                    preferredCallType = preferredCallType.name
                )
            )
        }.getOrNull()
        val dto = response?.takeIf { it.isSuccessful }?.body()
        if (dto != null) {
            CallSession(
                callId = dto.callId ?: UUID.randomUUID().toString(),
                fragileCitizenId = dto.fragileCitizenId ?: fragileCitizenId,
                citizenName = dto.citizenName ?: "Elena Martínez",
                citizenCode = dto.citizenCode ?: "ID #2847",
                deviceModel = dto.deviceModel ?: if (preferredCallType == CallType.VIDEO) "Guardian Pro" else "Guardian Band Lite",
                deviceId = dto.deviceId ?: "#PB-2847",
                callType = if (dto.callType == "VOICE") CallType.VOICE else preferredCallType,
                status = CallStatus.CONNECTED,
                degradedFromVideo = dto.degradedFromVideo ?: (preferredCallType == CallType.VOICE),
                startedAt = dto.startedAt ?: Instant.now().toString(),
                vitals = LiveVitalsSnapshot(
                    bpm = dto.bpm ?: 76,
                    spo2Percentage = dto.spo2Percentage ?: 97,
                    temperatureCelsius = dto.temperatureCelsius ?: 36.7,
                    bloodPressure = dto.bloodPressure ?: "120/80"
                )
            )
        } else {
            CallSession(
                callId = UUID.randomUUID().toString(),
                fragileCitizenId = fragileCitizenId,
                citizenName = "Elena Martínez",
                citizenCode = "ID #2847",
                deviceModel = if (preferredCallType == CallType.VIDEO) "Guardian Pro" else "Guardian Band Lite",
                deviceId = "#PB-2847",
                callType = preferredCallType,
                status = CallStatus.CONNECTED,
                degradedFromVideo = preferredCallType == CallType.VOICE,
                startedAt = Instant.now().toString(),
                vitals = LiveVitalsSnapshot()
            )
        }
    }

    override suspend fun endCall(callId: String): Result<Unit> = runCatching {
        runCatching { api.endCall(callId) }
        Unit
    }

    override suspend fun registerUnansweredCallTimeout(
        fragileCitizenId: String,
        callId: String,
        waitDurationSeconds: Int
    ): Result<List<CallLogEntry>> = runCatching {
        val response = runCatching {
            api.registerUnansweredCallLog(
                RegisterCallLogRequestDto(
                    fragileCitizenId = fragileCitizenId,
                    callId = callId,
                    status = CallStatus.UNANSWERED_TIMEOUT.name,
                    reason = "No hubo respuesta en la pulsera receptora tras ${waitDurationSeconds}s de espera.",
                    waitDurationSeconds = waitDurationSeconds
                )
            )
        }.getOrNull()

        val remoteLogs = response?.takeIf { it.isSuccessful }?.body()
        if (!remoteLogs.isNullOrEmpty()) {
            remoteLogs.map { dto ->
                CallLogEntry(
                    id = dto.id ?: UUID.randomUUID().toString(),
                    fragileCitizenId = dto.fragileCitizenId ?: fragileCitizenId,
                    title = dto.title ?: "Videollamada perdida",
                    timestamp = dto.timestamp ?: "10:42:00 AM",
                    statusLabel = dto.statusLabel ?: "Sin respuesta",
                    isMissed = dto.isMissed ?: true
                )
            }
        } else {
            localCallLogs.toList()
        }
    }

    override suspend fun getCallLogs(fragileCitizenId: String): Result<List<CallLogEntry>> = runCatching {
        val response = runCatching { api.getCallLogs(fragileCitizenId) }.getOrNull()
        val remoteLogs = response?.takeIf { it.isSuccessful }?.body()
        if (!remoteLogs.isNullOrEmpty()) {
            remoteLogs.map { dto ->
                CallLogEntry(
                    id = dto.id ?: UUID.randomUUID().toString(),
                    fragileCitizenId = dto.fragileCitizenId ?: fragileCitizenId,
                    title = dto.title ?: "Videollamada perdida",
                    timestamp = dto.timestamp ?: "10:42:00 AM",
                    statusLabel = dto.statusLabel ?: "Sin respuesta",
                    isMissed = dto.isMissed ?: true
                )
            }
        } else {
            localCallLogs.toList()
        }
    }

    override suspend fun sendHapticAlert(fragileCitizenId: String): Result<Unit> = runCatching {
        runCatching {
            api.sendHapticAlert(HapticAlertRequestDto(fragileCitizenId = fragileCitizenId))
        }
        Unit
    }

    private fun buildFallbackCapabilities(
        capability: DeviceCommunicationCapability
    ): DeviceCapabilities {
        return when (capability) {
            DeviceCommunicationCapability.VIDEO_AND_AUDIO -> DeviceCapabilities(
                deviceId = "#2847",
                modelName = "Guardian Pro",
                citizenName = "Elena Martínez",
                citizenCode = "ID #2847",
                capability = DeviceCommunicationCapability.VIDEO_AND_AUDIO,
                hasCamera = true,
                hasBidirectionalAudio = true,
                hasSpeaker = true,
                hasMicrophone = true,
                hasHapticAlerts = true,
                hasTelemetry = true
            )
            DeviceCommunicationCapability.AUDIO_ONLY -> DeviceCapabilities(
                deviceId = "#PB-2847",
                modelName = "Guardian Band Lite",
                citizenName = "Elena Martínez",
                citizenCode = "ID #PB-2847",
                capability = DeviceCommunicationCapability.AUDIO_ONLY,
                hasCamera = false,
                hasBidirectionalAudio = true,
                hasSpeaker = true,
                hasMicrophone = true,
                hasHapticAlerts = true,
                hasTelemetry = true
            )
            DeviceCommunicationCapability.TELEMETRY_ONLY -> DeviceCapabilities(
                deviceId = "#GC-2847",
                modelName = "Guardian Core",
                citizenName = "Elena Martínez",
                citizenCode = "ID #GC-2847",
                capability = DeviceCommunicationCapability.TELEMETRY_ONLY,
                hasCamera = false,
                hasBidirectionalAudio = false,
                hasSpeaker = false,
                hasMicrophone = false,
                hasHapticAlerts = true,
                hasTelemetry = true
            )
        }
    }
}
