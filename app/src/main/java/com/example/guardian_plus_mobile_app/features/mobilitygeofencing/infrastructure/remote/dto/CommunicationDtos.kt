package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName

data class DeviceCapabilitiesDto(
    @SerializedName("deviceId") val deviceId: String?,
    @SerializedName("modelName") val modelName: String?,
    @SerializedName("citizenName") val citizenName: String?,
    @SerializedName("citizenCode") val citizenCode: String?,
    @SerializedName("capability") val capability: String?,
    @SerializedName("hasCamera") val hasCamera: Boolean?,
    @SerializedName("hasBidirectionalAudio") val hasBidirectionalAudio: Boolean?,
    @SerializedName("hasSpeaker") val hasSpeaker: Boolean?,
    @SerializedName("hasMicrophone") val hasMicrophone: Boolean?,
    @SerializedName("hasHapticAlerts") val hasHapticAlerts: Boolean?,
    @SerializedName("hasTelemetry") val hasTelemetry: Boolean?,
    @SerializedName("citizenPhoneNumber") val citizenPhoneNumber: String?,
    @SerializedName("caregiverName") val caregiverName: String?,
    @SerializedName("caregiverRole") val caregiverRole: String?,
    @SerializedName("caregiverPhoneNumber") val caregiverPhoneNumber: String?
)

data class StartCallRequestDto(
    @SerializedName("fragileCitizenId") val fragileCitizenId: String,
    @SerializedName("preferredCallType") val preferredCallType: String
)

data class CallSessionDto(
    @SerializedName("callId") val callId: String?,
    @SerializedName("fragileCitizenId") val fragileCitizenId: String?,
    @SerializedName("citizenName") val citizenName: String?,
    @SerializedName("citizenCode") val citizenCode: String?,
    @SerializedName("deviceModel") val deviceModel: String?,
    @SerializedName("deviceId") val deviceId: String?,
    @SerializedName("callType") val callType: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("degradedFromVideo") val degradedFromVideo: Boolean?,
    @SerializedName("startedAt") val startedAt: String?,
    @SerializedName("bpm") val bpm: Int?,
    @SerializedName(value = "spo2Percentage", alternate = ["spo2"]) val spo2Percentage: Int?,
    @SerializedName(value = "temperatureCelsius", alternate = ["temperature"]) val temperatureCelsius: Double?,
    @SerializedName("bloodPressure") val bloodPressure: String?
)

data class RegisterCallLogRequestDto(
    @SerializedName("fragileCitizenId") val fragileCitizenId: String,
    @SerializedName("callId") val callId: String,
    @SerializedName("status") val status: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("waitDurationSeconds") val waitDurationSeconds: Int = 30
)

data class CallLogEntryDto(
    @SerializedName("id") val id: String?,
    @SerializedName("fragileCitizenId") val fragileCitizenId: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("statusLabel") val statusLabel: String?,
    @SerializedName(value = "isMissed", alternate = ["missed"]) val isMissed: Boolean?
)

data class HapticAlertRequestDto(
    @SerializedName("fragileCitizenId") val fragileCitizenId: String,
    @SerializedName("pattern") val pattern: String = "BUZZ_URGENT"
)
