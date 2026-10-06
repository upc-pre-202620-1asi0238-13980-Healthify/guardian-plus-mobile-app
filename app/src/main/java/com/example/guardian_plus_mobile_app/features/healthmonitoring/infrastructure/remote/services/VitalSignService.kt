package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto.LiveVitalSignsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface VitalSignService {
    @GET("vital-signs/live/{careRecipientProfileId}")
    suspend fun getLiveVitalSigns(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<LiveVitalSignsDto>
}
