package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface MobilityGeofencingApi {

    @GET("location-tracking/{fragileCitizenId}/current")
    suspend fun getCurrentLocation(
        @Path("fragileCitizenId") fragileCitizenId: String
    ): Response<CurrentLocationDto>

    @GET("location-tracking/{fragileCitizenId}/status")
    suspend fun getCurrentStatus(
        @Path("fragileCitizenId") fragileCitizenId: String
    ): Response<String>

    @GET("location-tracking/{fragileCitizenId}/history")
    suspend fun getLocationHistory(
        @Path("fragileCitizenId") fragileCitizenId: String,
        @Query("start") start: String?,
        @Query("end") end: String?
    ): Response<List<LocationHistoryDto>>

    @GET("safe-zones/fragile-citizen/{fragileCitizenId}/active")
    suspend fun getActiveSafeZone(
        @Path("fragileCitizenId") fragileCitizenId: String
    ): Response<SafeZoneDto>

    @POST("safe-zones")
    suspend fun createSafeZone(
        @Body request: CreateSafeZoneRequest
    ): Response<String>

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
}