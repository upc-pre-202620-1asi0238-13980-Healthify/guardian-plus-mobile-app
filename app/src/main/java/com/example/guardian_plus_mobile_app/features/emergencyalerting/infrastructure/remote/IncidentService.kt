package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IncidentService {

    @GET("incidents/alert/{alertId}")
    suspend fun getIncidentByAlertId(@Path("alertId") alertId: String): Response<IncidentDto>

    @POST("incidents/{incidentId}/stabilize")
    suspend fun stabilizeIncident(
        @Path("incidentId") incidentId: String,
        @Body request: NotesRequestDto
    ): Response<IncidentDto>

    @POST("incidents/{incidentId}/close")
    suspend fun closeIncident(
        @Path("incidentId") incidentId: String,
        @Body request: NotesRequestDto
    ): Response<IncidentDto>
}
