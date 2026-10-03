package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

interface EmergencyContactRepository {
    suspend fun getEmergencyContacts(careRecipientProfileId: String): Result<List<EmergencyContact>>
}
