package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AlertSettingsService {

    @GET("alert-settings/care-recipient/{careRecipientProfileId}")
    suspend fun getAlertSettings(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<AlertSettingsDto>
}
