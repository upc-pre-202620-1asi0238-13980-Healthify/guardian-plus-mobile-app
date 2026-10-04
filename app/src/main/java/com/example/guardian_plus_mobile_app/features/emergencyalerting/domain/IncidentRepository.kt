package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

interface IncidentRepository {
    /** Succeeds with null when nobody has acknowledged the alert yet, so it has no incident. */
    suspend fun getIncidentByAlertId(alertId: String): Result<Incident?>
    suspend fun stabilizeIncident(incidentId: String, notes: String?): Result<Incident>
    suspend fun closeIncident(incidentId: String, notes: String?): Result<Incident>
}
