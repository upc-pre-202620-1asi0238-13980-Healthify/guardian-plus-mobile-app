package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto.LinkWearableDeviceRequestDto
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto.WearableDeviceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WearableDeviceService {

    @GET("wearable-devices/care-recipient/{careRecipientProfileId}")
    suspend fun getWearableDevices(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<List<WearableDeviceDto>>

    /** Links a device to a care recipient; 409 if its serial number is already linked. */
    @POST("wearable-devices")
    suspend fun linkWearableDevice(@Body request: LinkWearableDeviceRequestDto): Response<WearableDeviceDto>
}
