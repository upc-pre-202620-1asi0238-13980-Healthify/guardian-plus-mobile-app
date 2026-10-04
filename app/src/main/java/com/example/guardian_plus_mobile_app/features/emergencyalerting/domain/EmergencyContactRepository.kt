package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

interface EmergencyContactRepository {
    suspend fun getEmergencyContacts(careRecipientProfileId: String): Result<List<EmergencyContact>>
    suspend fun addEmergencyContact(
        careRecipientProfileId: String,
        userId: String,
        displayName: String,
        relationship: String,
        phoneNumber: String
    ): Result<EmergencyContact>
    suspend fun removeEmergencyContact(emergencyContactId: String): Result<EmergencyContact>
    /** [orderedIds] must list every active contact exactly once; the first one becomes the primary contact. */
    suspend fun reorderEmergencyContacts(
        careRecipientProfileId: String,
        orderedIds: List<String>
    ): Result<List<EmergencyContact>>
}
