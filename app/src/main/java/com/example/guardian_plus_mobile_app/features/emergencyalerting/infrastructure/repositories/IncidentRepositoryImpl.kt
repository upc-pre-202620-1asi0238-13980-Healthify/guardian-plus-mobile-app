package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Incident
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.IncidentService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.NotesRequestDto
import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.toDomain
import javax.inject.Inject

class IncidentRepositoryImpl @Inject constructor(
    private val service: IncidentService
) : IncidentRepository {

    // 404 is expected: an alert only has an incident once someone acknowledged it
    override suspend fun getIncidentByAlertId(alertId: String): Result<Incident?> =
        apiCall({ service.getIncidentByAlertId(alertId) }, onNotFound = { null }) { dto -> dto.toDomain() }

    override suspend fun stabilizeIncident(incidentId: String, notes: String?): Result<Incident> =
        apiCall({ service.stabilizeIncident(incidentId, NotesRequestDto(notes)) }) { dto -> dto.toDomain() }

    override suspend fun closeIncident(incidentId: String, notes: String?): Result<Incident> =
        apiCall({ service.closeIncident(incidentId, NotesRequestDto(notes)) }) { dto -> dto.toDomain() }
}
