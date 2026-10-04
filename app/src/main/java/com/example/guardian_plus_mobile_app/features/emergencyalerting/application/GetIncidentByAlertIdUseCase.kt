package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Incident
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentRepository
import javax.inject.Inject

class GetIncidentByAlertIdUseCase @Inject constructor(
    private val repository: IncidentRepository
) {
    suspend operator fun invoke(alertId: String): Result<Incident?> = repository.getIncidentByAlertId(alertId)
}
