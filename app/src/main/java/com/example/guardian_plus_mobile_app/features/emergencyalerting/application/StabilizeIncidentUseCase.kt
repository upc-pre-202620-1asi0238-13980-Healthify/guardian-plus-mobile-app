package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Incident
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentRepository
import javax.inject.Inject

/** The person under care is out of danger, but the incident stays open until someone closes it. */
class StabilizeIncidentUseCase @Inject constructor(
    private val repository: IncidentRepository
) {
    suspend operator fun invoke(incidentId: String, notes: String?): Result<Incident> =
        repository.stabilizeIncident(incidentId, notes)
}
