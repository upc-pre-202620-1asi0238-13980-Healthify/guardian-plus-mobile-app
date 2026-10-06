package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContactRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.AddEmergencyContactRequestDto
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.EmergencyContactService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.ReorderEmergencyContactsRequestDto
import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.toDomain
import javax.inject.Inject

class EmergencyContactRepositoryImpl @Inject constructor(
    private val service: EmergencyContactService
) : EmergencyContactRepository {

    override suspend fun getEmergencyContacts(careRecipientProfileId: String): Result<List<EmergencyContact>> =
        apiCall({ service.getEmergencyContacts(careRecipientProfileId) }) { dtos -> dtos.map { it.toDomain() } }

    override suspend fun addEmergencyContact(
        careRecipientProfileId: String,
        userId: String,
        displayName: String,
        relationship: String,
        phoneNumber: String
    ): Result<EmergencyContact> = apiCall({
        service.addEmergencyContact(
            AddEmergencyContactRequestDto(careRecipientProfileId, userId, displayName, relationship, phoneNumber)
        )
    }) { dto -> dto.toDomain() }

    override suspend fun removeEmergencyContact(emergencyContactId: String): Result<EmergencyContact> =
        apiCall({ service.removeEmergencyContact(emergencyContactId) }) { dto -> dto.toDomain() }

    override suspend fun reorderEmergencyContacts(
        careRecipientProfileId: String,
        orderedIds: List<String>
    ): Result<List<EmergencyContact>> = apiCall({
        service.reorderEmergencyContacts(careRecipientProfileId, ReorderEmergencyContactsRequestDto(orderedIds))
    }) { dtos -> dtos.map { it.toDomain() } }
}
