package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.SleepCycleRecordDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SleepCycleRecordService {

    @GET("sleep-cycle-records/citizen/{personUnderCareId}")
    suspend fun getSleepCycleRecords(
        @Path("personUnderCareId") personUnderCareId: String,
        @Query("from") from: String?
    ): Response<List<SleepCycleRecordDto>>
}
