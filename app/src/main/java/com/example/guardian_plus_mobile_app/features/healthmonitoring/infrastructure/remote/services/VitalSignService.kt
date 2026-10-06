package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto.LiveVitalSignsDto
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto.VitalSignDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface VitalSignService {
    @GET("vital-signs/live/{careRecipientProfileId}")
    suspend fun getLiveVitalSigns(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<LiveVitalSignsDto>


    @GET("vital-signs/history/{careRecipientProfileId}")
    suspend fun getVitalSignHistory(
        @Path("careRecipientProfileId") careRecipientProfileId: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<List<VitalSignDto>> 
    
}
