package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model

enum class DeviceCommunicationCapability {
    VIDEO_AND_AUDIO,
    AUDIO_ONLY,
    TELEMETRY_ONLY
}

enum class CallType {
    VIDEO,
    VOICE
}

enum class CallStatus {
    INITIATED,
    RINGING,
    CONNECTED,
    UNANSWERED_TIMEOUT,
    ENDED,
    FALLBACK_TO_VOICE
}

data class DeviceCapabilities(
    val deviceId: String,
    val modelName: String,
    val citizenName: String,
    val citizenCode: String,
    val capability: DeviceCommunicationCapability,
    val hasCamera: Boolean,
    val hasBidirectionalAudio: Boolean,
    val hasSpeaker: Boolean,
    val hasMicrophone: Boolean,
    val hasHapticAlerts: Boolean,
    val hasTelemetry: Boolean,
    val citizenPhoneNumber: String = "+51 987 654 321",
    val caregiverName: String = "Rosa Gómez",
    val caregiverRole: String = "Cuidadora",
    val caregiverPhoneNumber: String = "+51 912 345 678"
)

data class LiveVitalsSnapshot(
    val bpm: Int = 76,
    val spo2Percentage: Int = 97,
    val temperatureCelsius: Double = 36.7,
    val bloodPressure: String = "120/80"
)

data class CallSession(
    val callId: String,
    val fragileCitizenId: String,
    val citizenName: String,
    val citizenCode: String,
    val deviceModel: String,
    val deviceId: String,
    val callType: CallType,
    val status: CallStatus,
    val degradedFromVideo: Boolean = false,
    val startedAt: String,
    val vitals: LiveVitalsSnapshot = LiveVitalsSnapshot()
)

data class CallLogEntry(
    val id: String,
    val fragileCitizenId: String,
    val title: String,
    val timestamp: String,
    val statusLabel: String,
    val isMissed: Boolean
)
