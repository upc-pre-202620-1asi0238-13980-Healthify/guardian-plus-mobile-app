package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.repository

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallLogEntry
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallSession
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallType
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CurrentLocation
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.DeviceCapabilities
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.DeviceCommunicationCapability
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
    suspend fun getSafeZonesByCitizen(fragileCitizenId: String): Result<List<SafeZone>>

    // US-23: Direct Communication Channel
    suspend fun getDeviceCapabilities(
        fragileCitizenId: String,
        simulatedCapability: DeviceCommunicationCapability? = null
    ): Result<DeviceCapabilities>

    suspend fun startCall(
        fragileCitizenId: String,
        preferredCallType: CallType
    ): Result<CallSession>

    suspend fun endCall(callId: String): Result<Unit>

    suspend fun registerUnansweredCallTimeout(
        fragileCitizenId: String,
        callId: String,
        waitDurationSeconds: Int = 30
    ): Result<List<CallLogEntry>>

    suspend fun getCallLogs(fragileCitizenId: String): Result<List<CallLogEntry>>

    suspend fun sendHapticAlert(fragileCitizenId: String): Result<Unit>
}
