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

    // US-23: Direct Communication Channel
    override suspend fun getDeviceCapabilities(
        fragileCitizenId: String
    ): Result<DeviceCapabilities> = runCatching {
        val response = api.getDeviceCapabilities(fragileCitizenId)
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                ?: "Error al consultar las capacidades del dispositivo (${response.code()})"
            throw Exception(errorMsg)
        }
        val dto = response.body() ?: throw Exception("Respuesta vacía al consultar capacidades del dispositivo.")
        val capability = when (dto.capability) {
            "AUDIO_ONLY" -> DeviceCommunicationCapability.AUDIO_ONLY
            "TELEMETRY_ONLY" -> DeviceCommunicationCapability.TELEMETRY_ONLY
            "VIDEO_AND_AUDIO" -> DeviceCommunicationCapability.VIDEO_AND_AUDIO
            else -> DeviceCommunicationCapability.valueOf(dto.capability ?: "VIDEO_AND_AUDIO")
        }
        DeviceCapabilities(
            deviceId = dto.deviceId ?: "",
            modelName = dto.modelName ?: "",
            citizenName = dto.citizenName ?: "",
            citizenCode = dto.citizenCode ?: "",
            capability = capability,
            hasCamera = dto.hasCamera ?: (capability == DeviceCommunicationCapability.VIDEO_AND_AUDIO),
            hasBidirectionalAudio = dto.hasBidirectionalAudio ?: (capability != DeviceCommunicationCapability.TELEMETRY_ONLY),
            hasSpeaker = dto.hasSpeaker ?: (capability != DeviceCommunicationCapability.TELEMETRY_ONLY),
            hasMicrophone = dto.hasMicrophone ?: (capability != DeviceCommunicationCapability.TELEMETRY_ONLY),
            hasHapticAlerts = dto.hasHapticAlerts ?: true,
            hasTelemetry = dto.hasTelemetry ?: true,
            citizenPhoneNumber = dto.citizenPhoneNumber ?: "",
            caregiverName = dto.caregiverName ?: "",
            caregiverRole = dto.caregiverRole ?: "",
            caregiverPhoneNumber = dto.caregiverPhoneNumber ?: ""
        )
    }

    override suspend fun startCall(
        fragileCitizenId: String,
        preferredCallType: CallType
    ): Result<CallSession> = runCatching {
        val response = api.startCall(
            StartCallRequestDto(
                fragileCitizenId = fragileCitizenId,
                preferredCallType = preferredCallType.name
            )
        )
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                ?: "Error al iniciar la llamada (${response.code()})"
            throw Exception(errorMsg)
        }
        val dto = response.body() ?: throw Exception("Respuesta vacía del servidor al iniciar llamada.")
        CallSession(
            callId = dto.callId ?: "",
            fragileCitizenId = dto.fragileCitizenId ?: fragileCitizenId,
            citizenName = dto.citizenName ?: "",
            citizenCode = dto.citizenCode ?: "",
            deviceModel = dto.deviceModel ?: "",
            deviceId = dto.deviceId ?: "",
            callType = if (dto.callType == "VOICE") CallType.VOICE else CallType.VIDEO,
            status = runCatching { CallStatus.valueOf(dto.status ?: "CONNECTED") }.getOrDefault(CallStatus.CONNECTED),
            degradedFromVideo = dto.degradedFromVideo ?: false,
            startedAt = dto.startedAt ?: "",
            vitals = LiveVitalsSnapshot(
                bpm = dto.bpm ?: 0,
                spo2Percentage = dto.spo2Percentage ?: 0,
                temperatureCelsius = dto.temperatureCelsius ?: 0.0,
                bloodPressure = dto.bloodPressure ?: ""
            )
        )
    }

    override suspend fun endCall(callId: String): Result<Unit> = runCatching {
        val response = api.endCall(callId)
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                ?: "Error al finalizar la llamada (${response.code()})"
            throw Exception(errorMsg)
        }
        Unit
    }

    override suspend fun registerUnansweredCallTimeout(
        fragileCitizenId: String,
        callId: String,
        waitDurationSeconds: Int
    ): Result<List<CallLogEntry>> = runCatching {
        val response = api.registerUnansweredCallLog(
            RegisterCallLogRequestDto(
                fragileCitizenId = fragileCitizenId,
                callId = callId,
                status = CallStatus.UNANSWERED_TIMEOUT.name,
                reason = "No hubo respuesta en la pulsera receptora tras ${waitDurationSeconds}s de espera.",
                waitDurationSeconds = waitDurationSeconds
            )
        )
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                ?: "Error al registrar llamada no atendida en bitácora (${response.code()})"
            throw Exception(errorMsg)
        }
        val remoteLogs = response.body() ?: emptyList()
        remoteLogs.map { dto ->
            CallLogEntry(
                id = dto.id ?: "",
                fragileCitizenId = dto.fragileCitizenId ?: fragileCitizenId,
                title = dto.title ?: "",
                timestamp = dto.timestamp ?: "",
                statusLabel = dto.statusLabel ?: "",
                isMissed = dto.isMissed ?: false
            )
        }
    }

    override suspend fun getCallLogs(fragileCitizenId: String): Result<List<CallLogEntry>> = runCatching {
        val response = api.getCallLogs(fragileCitizenId)
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                ?: "Error al consultar el historial de llamadas (${response.code()})"
            throw Exception(errorMsg)
        }
        val remoteLogs = response.body() ?: emptyList()
        remoteLogs.map { dto ->
            CallLogEntry(
                id = dto.id ?: "",
                fragileCitizenId = dto.fragileCitizenId ?: fragileCitizenId,
                title = dto.title ?: "",
                timestamp = dto.timestamp ?: "",
                statusLabel = dto.statusLabel ?: "",
                isMissed = dto.isMissed ?: false
            )
        }
    }

    override suspend fun sendHapticAlert(fragileCitizenId: String): Result<Unit> = runCatching {
        val response = api.sendHapticAlert(HapticAlertRequestDto(fragileCitizenId = fragileCitizenId))
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                ?: "Error al enviar alerta vibratoria (${response.code()})"
            throw Exception(errorMsg)
        }
        Unit
    }
}
