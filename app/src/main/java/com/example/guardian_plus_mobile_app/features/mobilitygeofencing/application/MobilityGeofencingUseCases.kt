package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallLogEntry
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallSession
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallType
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CurrentLocation
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.DeviceCapabilities
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.DeviceCommunicationCapability
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

    suspend fun verifyDeviceCapabilities(
        fragileCitizenId: String,
        simulatedCapability: DeviceCommunicationCapability? = null
    ): Result<DeviceCapabilities> {
        return repository.getDeviceCapabilities(fragileCitizenId, simulatedCapability)
    }

    suspend fun startDirectCall(
        fragileCitizenId: String,
        preferredCallType: CallType
    ): Result<CallSession> {
        return repository.startCall(fragileCitizenId, preferredCallType)
    }

    suspend fun registerUnansweredTimeout(
        fragileCitizenId: String,
        callId: String,
        waitDurationSeconds: Int = 30
    ): Result<List<CallLogEntry>> {
        return repository.registerUnansweredCallTimeout(fragileCitizenId, callId, waitDurationSeconds)
    }

    suspend fun sendHapticBuzz(fragileCitizenId: String): Result<Unit> {
        return repository.sendHapticAlert(fragileCitizenId)
    }
}
