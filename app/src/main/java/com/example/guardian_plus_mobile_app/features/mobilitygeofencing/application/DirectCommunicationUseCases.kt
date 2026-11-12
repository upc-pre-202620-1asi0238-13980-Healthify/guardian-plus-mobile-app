package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallLogEntry
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallSession
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CallType
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.DeviceCapabilities
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.DeviceCommunicationCapability
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.repository.MobilityGeofencingRepository
import javax.inject.Inject

sealed interface DirectCommunicationRoutingResult {
    data class StartVideoCall(
        val session: CallSession,
        val capabilities: DeviceCapabilities
    ) : DirectCommunicationRoutingResult

    data class FallbackToVoiceCall(
        val session: CallSession,
        val capabilities: DeviceCapabilities
    ) : DirectCommunicationRoutingResult

    data class TelemetryOnlyDevice(
        val capabilities: DeviceCapabilities
    ) : DirectCommunicationRoutingResult
}

class VerifyDeviceCapabilitiesUseCase @Inject constructor(
    private val repository: MobilityGeofencingRepository
) {
    suspend operator fun invoke(fragileCitizenId: String): Result<DeviceCapabilities> {
        return repository.getDeviceCapabilities(fragileCitizenId)
    }
}

class InitiateDirectCommunicationUseCase @Inject constructor(
    private val repository: MobilityGeofencingRepository
) {
    suspend operator fun invoke(
        fragileCitizenId: String
    ): Result<DirectCommunicationRoutingResult> = runCatching {
        val capabilities = repository
            .getDeviceCapabilities(fragileCitizenId)
            .getOrThrow()

        when (capabilities.capability) {
            DeviceCommunicationCapability.VIDEO_AND_AUDIO -> {
                val session = repository.startCall(fragileCitizenId, CallType.VIDEO).getOrThrow()
                DirectCommunicationRoutingResult.StartVideoCall(session, capabilities)
            }
            DeviceCommunicationCapability.AUDIO_ONLY -> {
                val session = repository.startCall(fragileCitizenId, CallType.VOICE).getOrThrow()
                DirectCommunicationRoutingResult.FallbackToVoiceCall(
                    session = session.copy(degradedFromVideo = true),
                    capabilities = capabilities
                )
            }
            DeviceCommunicationCapability.TELEMETRY_ONLY -> {
                DirectCommunicationRoutingResult.TelemetryOnlyDevice(capabilities)
            }
        }
    }
}

class ManageCallTimeoutUseCase @Inject constructor(
    private val repository: MobilityGeofencingRepository
) {
    companion object {
        const val CALL_TIMEOUT_SECONDS = 30
    }

    suspend fun registerTimeoutInLog(
        fragileCitizenId: String,
        callId: String,
        waitDurationSeconds: Int = CALL_TIMEOUT_SECONDS
    ): Result<List<CallLogEntry>> {
        return repository.registerUnansweredCallTimeout(
            fragileCitizenId = fragileCitizenId,
            callId = callId,
            waitDurationSeconds = waitDurationSeconds
        )
    }

    suspend fun getCallHistoryLogs(fragileCitizenId: String): Result<List<CallLogEntry>> {
        return repository.getCallLogs(fragileCitizenId)
    }

    suspend fun sendHapticBuzzAlert(fragileCitizenId: String): Result<Unit> {
        return repository.sendHapticAlert(fragileCitizenId)
    }

    suspend fun endActiveCall(callId: String): Result<Unit> {
        return repository.endCall(callId)
    }
}
