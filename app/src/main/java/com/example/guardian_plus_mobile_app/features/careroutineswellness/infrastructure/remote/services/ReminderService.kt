package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ReminderDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ScheduleReminderRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ReminderService {

    @GET("reminders/citizen/{personUnderCareId}")
    suspend fun getReminders(
        @Path("personUnderCareId") personUnderCareId: String,
        @Query("from") from: String?,
        @Query("to") to: String?,
        @Query("type") type: String?
    ): Response<List<ReminderDto>>

    @POST("reminders")
    suspend fun scheduleReminder(@Body request: ScheduleReminderRequestDto): Response<ReminderDto>

    @PUT("reminders/{reminderId}/confirm")
    suspend fun confirmReminder(@Path("reminderId") reminderId: String): Response<ReminderDto>

    @PUT("reminders/{reminderId}/miss")
    suspend fun markReminderAsMissed(@Path("reminderId") reminderId: String): Response<ReminderDto>

    @DELETE("reminders/{reminderId}")
    suspend fun cancelReminder(@Path("reminderId") reminderId: String): Response<ReminderDto>
}
