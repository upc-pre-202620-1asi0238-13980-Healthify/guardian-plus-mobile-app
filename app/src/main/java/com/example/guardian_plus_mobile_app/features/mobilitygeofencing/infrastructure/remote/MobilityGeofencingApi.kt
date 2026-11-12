package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto.*
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

data class LocationStatusDto(
    @SerializedName("fragileCitizenId") val fragileCitizenId: String,
    @SerializedName("status") val status: String
)

interface MobilityGeofencingApi {

    @GET("location-tracking/{fragileCitizenId}/current")
    suspend fun getCurrentLocation(
        @Path("fragileCitizenId") fragileCitizenId: String
    ): Response<CurrentLocationDto>

    @GET("location-tracking/{fragileCitizenId}/status")
    suspend fun getCurrentStatus(
        @Path("fragileCitizenId") fragileCitizenId: String
    ): Response<LocationStatusDto>

    @GET("location-tracking/{fragileCitizenId}/history")
    suspend fun getLocationHistory(
        @Path("fragileCitizenId") fragileCitizenId: String,
        @Query("start") start: String?,
        @Query("end") end: String?
    ): Response<List<LocationHistoryDto>>

    @GET("safe-zones/fragile-citizen/{fragileCitizenId}")
    suspend fun getSafeZonesByCitizen(
        @Path("fragileCitizenId") fragileCitizenId: String
    ): Response<List<SafeZoneDto>>

    @GET("safe-zones/fragile-citizen/{fragileCitizenId}/active")
    suspend fun getActiveSafeZone(
        @Path("fragileCitizenId") fragileCitizenId: String
    ): Response<SafeZoneDto>

    @POST("safe-zones")
    suspend fun createSafeZone(
        @Body request: CreateSafeZoneRequest
    ): Response<ResponseBody>

    @PUT("safe-zones/{safeZoneId}")
    suspend fun updateSafeZone(
        @Path("safeZoneId") safeZoneId: String,
        @Body request: UpdateSafeZoneRequest
    ): Response<Unit>

    @PATCH("safe-zones/{safeZoneId}/activate")
    suspend fun activateSafeZone(
        @Path("safeZoneId") safeZoneId: String
    ): Response<Unit>

    @PATCH("safe-zones/{safeZoneId}/deactivate")
    suspend fun deactivateSafeZone(
        @Path("safeZoneId") safeZoneId: String
    ): Response<Unit>

    @DELETE("safe-zones/{safeZoneId}")
    suspend fun deleteSafeZone(
        @Path("safeZoneId") safeZoneId: String
    ): Response<Unit>

    @GET("communication/{fragileCitizenId}/device-capabilities")
    suspend fun getDeviceCapabilities(
        @Path("fragileCitizenId") fragileCitizenId: String
    ): Response<DeviceCapabilitiesDto>

    @POST("communication/calls/start")
    suspend fun startCall(
        @Body request: StartCallRequestDto
    ): Response<CallSessionDto>

    @POST("communication/calls/{callId}/end")
    suspend fun endCall(
        @Path("callId") callId: String
    ): Response<Unit>

    @POST("communication/calls/log-unanswered")
    suspend fun registerUnansweredCallLog(
        @Body request: RegisterCallLogRequestDto
    ): Response<List<CallLogEntryDto>>

    @GET("communication/{fragileCitizenId}/call-logs")
    suspend fun getCallLogs(
        @Path("fragileCitizenId") fragileCitizenId: String
    ): Response<List<CallLogEntryDto>>

    @POST("communication/haptic-alert")
    suspend fun sendHapticAlert(
        @Body request: HapticAlertRequestDto
    ): Response<Unit>
}
