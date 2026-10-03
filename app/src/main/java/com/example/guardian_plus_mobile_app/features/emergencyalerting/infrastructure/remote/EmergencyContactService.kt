package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EmergencyContactService {

    @GET("emergency-contacts/care-recipient/{careRecipientProfileId}")
    suspend fun getEmergencyContacts(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<List<EmergencyContactDto>>

    @POST("emergency-contacts")
    suspend fun addEmergencyContact(@Body request: AddEmergencyContactRequestDto): Response<EmergencyContactDto>

    @PUT("emergency-contacts/care-recipient/{careRecipientProfileId}/order")
    suspend fun reorderEmergencyContacts(
        @Path("careRecipientProfileId") careRecipientProfileId: String,
        @Body request: ReorderEmergencyContactsRequestDto
    ): Response<List<EmergencyContactDto>>

    @DELETE("emergency-contacts/{emergencyContactId}")
    suspend fun removeEmergencyContact(
        @Path("emergencyContactId") emergencyContactId: String
    ): Response<EmergencyContactDto>
}
