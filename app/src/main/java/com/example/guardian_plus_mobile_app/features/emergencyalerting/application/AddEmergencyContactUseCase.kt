package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContactRepository
import java.util.UUID
import javax.inject.Inject

class AddEmergencyContactUseCase @Inject constructor(
    private val repository: EmergencyContactRepository
) {
    /**
     * The platform links every contact to a Care Circle member account. Until the Profile bounded
     * context lets the user pick an existing member, each new contact stands for a new member.
     */
    suspend operator fun invoke(
        careRecipientProfileId: String,
        displayName: String,
        relationship: String,
        phoneNumber: String
    ): Result<EmergencyContact> = repository.addEmergencyContact(
        careRecipientProfileId = careRecipientProfileId,
        userId = UUID.randomUUID().toString(),
        displayName = displayName.trim(),
        relationship = relationship.trim(),
        phoneNumber = phoneNumber.trim()
    )
}
