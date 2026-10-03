package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AlertSettingsService {

    @GET("alert-settings/care-recipient/{careRecipientProfileId}")
    suspend fun getAlertSettings(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<AlertSettingsDto>

    @PUT("alert-settings/care-recipient/{careRecipientProfileId}")
    suspend fun updateAlertSettings(
        @Path("careRecipientProfileId") careRecipientProfileId: String,
        @Body request: UpdateAlertSettingsRequestDto
    ): Response<AlertSettingsDto>

    @POST("alert-settings/care-recipient/{careRecipientProfileId}/silent-mode")
    suspend fun activateSilentMode(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<AlertSettingsDto>

    @DELETE("alert-settings/care-recipient/{careRecipientProfileId}/silent-mode")
    suspend fun deactivateSilentMode(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<AlertSettingsDto>
}
