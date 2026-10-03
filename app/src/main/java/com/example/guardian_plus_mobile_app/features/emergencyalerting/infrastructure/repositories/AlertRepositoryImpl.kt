package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertPage
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.AcknowledgeAlertRequestDto
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.AlertService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.ClaimAlertResponseRequestDto
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.NotesRequestDto
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.apiCall
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.toDomain
import java.time.Instant
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val service: AlertService
) : AlertRepository {

    // Retrofit leaves out null query parameters, so an absent filter is simply not sent
    override suspend fun getAlertHistory(
        careRecipientProfileId: String,
        severity: Severity?,
        from: Instant?,
        to: Instant?,
        page: Int,
        size: Int
    ): Result<AlertPage> = apiCall({
        service.getAlertHistory(careRecipientProfileId, severity?.name, from?.toString(), to?.toString(), page, size)
    }) { dto -> dto.toDomain() }

    override suspend fun getActiveAlerts(careRecipientProfileId: String): Result<List<Alert>> =
        apiCall({ service.getActiveAlerts(careRecipientProfileId) }) { dtos -> dtos.map { it.toDomain() } }

    override suspend fun getPendingAlerts(userId: String): Result<List<Alert>> =
        apiCall({ service.getPendingAlerts(userId) }) { dtos -> dtos.map { it.toDomain() } }

    override suspend fun getAlertById(alertId: String): Result<Alert> =
        apiCall({ service.getAlertById(alertId) }) { dto -> dto.toDomain() }

    override suspend fun acknowledgeAlert(alertId: String, userId: String): Result<Alert> =
        apiCall({ service.acknowledgeAlert(alertId, AcknowledgeAlertRequestDto(userId)) }) { dto -> dto.toDomain() }

    override suspend fun claimAlertResponse(alertId: String, responderUserId: String): Result<Alert> =
        apiCall({ service.claimAlertResponse(alertId, ClaimAlertResponseRequestDto(responderUserId)) }) { dto ->
            dto.toDomain()
        }

    override suspend fun completeAlertResponse(alertId: String, responseId: String, notes: String?): Result<Alert> =
        apiCall({ service.completeAlertResponse(alertId, responseId, NotesRequestDto(notes)) }) { dto ->
            dto.toDomain()
        }
}
