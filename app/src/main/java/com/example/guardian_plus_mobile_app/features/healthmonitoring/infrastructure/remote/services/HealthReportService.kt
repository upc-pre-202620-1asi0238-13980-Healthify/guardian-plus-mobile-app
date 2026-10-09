package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto.GenerateHealthReportRequestDto
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto.HealthReportDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HealthReportService {

    @POST("health-reports")
    suspend fun generateHealthReport(@Body request: GenerateHealthReportRequestDto): Response<HealthReportDto>

    @GET("health-reports/care-recipient/{careRecipientProfileId}")
    suspend fun getHealthReports(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<List<HealthReportDto>>
}
