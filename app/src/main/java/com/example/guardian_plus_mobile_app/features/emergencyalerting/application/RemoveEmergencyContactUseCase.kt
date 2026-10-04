package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContactRepository
import javax.inject.Inject

/** The platform keeps at least one active contact, so removing the last one fails. */
class RemoveEmergencyContactUseCase @Inject constructor(
    private val repository: EmergencyContactRepository
) {
    suspend operator fun invoke(emergencyContactId: String): Result<EmergencyContact> =
        repository.removeEmergencyContact(emergencyContactId)
}
