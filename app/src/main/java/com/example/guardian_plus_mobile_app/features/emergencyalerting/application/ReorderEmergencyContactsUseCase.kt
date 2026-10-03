package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContactRepository
import javax.inject.Inject

/** Sets the escalation chain: the first contact is the primary one, the rest are secondary contacts. */
class ReorderEmergencyContactsUseCase @Inject constructor(
    private val repository: EmergencyContactRepository
) {
    suspend operator fun invoke(careRecipientProfileId: String, orderedIds: List<String>): Result<List<EmergencyContact>> =
        repository.reorderEmergencyContacts(careRecipientProfileId, orderedIds)
}
