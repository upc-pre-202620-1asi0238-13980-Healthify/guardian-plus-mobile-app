package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Incident
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentRepository
import javax.inject.Inject

/** Ends the incident; the backend then marks its alert as resolved. */
class CloseIncidentUseCase @Inject constructor(
    private val repository: IncidentRepository
) {
    suspend operator fun invoke(incidentId: String, notes: String?): Result<Incident> =
        repository.closeIncident(incidentId, notes)
}
