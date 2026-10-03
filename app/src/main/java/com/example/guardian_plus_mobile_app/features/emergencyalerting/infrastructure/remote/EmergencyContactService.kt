package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EmergencyContactService {

    @GET("emergency-contacts/care-recipient/{careRecipientProfileId}")
    suspend fun getEmergencyContacts(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<List<EmergencyContactDto>>
}
