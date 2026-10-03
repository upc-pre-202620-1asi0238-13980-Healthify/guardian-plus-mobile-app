package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AlertChannelSettingService {

    @GET("alert-channel-settings/user/{userId}")
    suspend fun getChannelSettings(@Path("userId") userId: String): Response<List<AlertChannelSettingDto>>

    @PUT("alert-channel-settings/user/{userId}/channels/{channel}")
    suspend fun configureChannel(
        @Path("userId") userId: String,
        @Path("channel") channel: String,
        @Body request: ConfigureAlertChannelRequestDto
    ): Response<AlertChannelSettingDto>
}
