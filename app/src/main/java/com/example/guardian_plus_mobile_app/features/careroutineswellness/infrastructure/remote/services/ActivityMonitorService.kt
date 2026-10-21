package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ActivityLogEntryDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ActivityMonitorDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ConfigureInactivityDetectionRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ActivityMonitorService {

    @GET("activity-monitors/citizen/{personUnderCareId}")
    suspend fun getActivityMonitor(@Path("personUnderCareId") personUnderCareId: String): Response<ActivityMonitorDto>

    @PUT("activity-monitors/citizen/{personUnderCareId}/inactivity-detection")
    suspend fun configureInactivityDetection(
        @Path("personUnderCareId") personUnderCareId: String,
        @Body request: ConfigureInactivityDetectionRequestDto
    ): Response<ActivityMonitorDto>

    @GET("activity-monitors/citizen/{personUnderCareId}/log-entries")
    suspend fun getActivityLog(
        @Path("personUnderCareId") personUnderCareId: String,
        @Query("limit") limit: Int
    ): Response<List<ActivityLogEntryDto>>
}
