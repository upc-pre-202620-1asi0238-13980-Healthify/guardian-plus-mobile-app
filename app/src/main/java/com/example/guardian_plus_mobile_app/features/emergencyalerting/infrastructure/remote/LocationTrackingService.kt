package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/** Mobility & Geofencing's last known position of the person under care, read to show where an alert happened. */
interface LocationTrackingService {

    // Mobility names the person under care "fragile citizen"; it is the care recipient profile id
    @GET("location-tracking/{fragileCitizenId}/current")
    suspend fun getCurrentLocation(@Path("fragileCitizenId") careRecipientProfileId: String): Response<CurrentLocationDto>
}
