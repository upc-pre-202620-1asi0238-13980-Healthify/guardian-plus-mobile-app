package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ConfigureHydrationPlanRequestDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.HydrationPlanDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface HydrationPlanService {

    @GET("hydration-plans/citizen/{personUnderCareId}")
    suspend fun getHydrationPlan(@Path("personUnderCareId") personUnderCareId: String): Response<HydrationPlanDto>

    @PUT("hydration-plans/citizen/{personUnderCareId}")
    suspend fun configureHydrationPlan(
        @Path("personUnderCareId") personUnderCareId: String,
        @Body request: ConfigureHydrationPlanRequestDto
    ): Response<HydrationPlanDto>
}
