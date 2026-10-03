package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContactRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.EmergencyContactService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.apiCall
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.toDomain
import javax.inject.Inject

class EmergencyContactRepositoryImpl @Inject constructor(
    private val service: EmergencyContactService
) : EmergencyContactRepository {

    override suspend fun getEmergencyContacts(careRecipientProfileId: String): Result<List<EmergencyContact>> =
        apiCall({ service.getEmergencyContacts(careRecipientProfileId) }) { dtos -> dtos.map { it.toDomain() } }
}
