package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.AdherenceDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AdherenceService {

    @GET("reminders/citizen/{personUnderCareId}/adherence")
    suspend fun getAdherence(
        @Path("personUnderCareId") personUnderCareId: String,
        @Query("type") type: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<AdherenceDto>
}
