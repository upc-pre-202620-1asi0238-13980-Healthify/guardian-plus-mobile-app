package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AlertService {

    @GET("alerts/active/care-recipient/{careRecipientProfileId}")
    suspend fun getActiveAlerts(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<List<AlertSummaryDto>>

    @GET("alerts/pending/recipient/{userId}")
    suspend fun getPendingAlerts(@Path("userId") userId: String): Response<List<AlertSummaryDto>>

    @GET("alerts/{alertId}")
    suspend fun getAlertById(@Path("alertId") alertId: String): Response<AlertDto>

    @POST("alerts/{alertId}/acknowledge")
    suspend fun acknowledgeAlert(
        @Path("alertId") alertId: String,
        @Body request: AcknowledgeAlertRequestDto
    ): Response<AlertDto>

    @POST("alerts/{alertId}/responses")
    suspend fun claimAlertResponse(
        @Path("alertId") alertId: String,
        @Body request: ClaimAlertResponseRequestDto
    ): Response<AlertDto>

    @POST("alerts/{alertId}/responses/{responseId}/complete")
    suspend fun completeAlertResponse(
        @Path("alertId") alertId: String,
        @Path("responseId") responseId: String,
        @Body request: NotesRequestDto
    ): Response<AlertDto>
}
