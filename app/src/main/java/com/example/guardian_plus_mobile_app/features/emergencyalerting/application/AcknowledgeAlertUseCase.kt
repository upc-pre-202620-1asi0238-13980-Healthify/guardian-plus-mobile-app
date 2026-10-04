package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertRepository
import javax.inject.Inject

/** Stops the escalation and opens the incident in attention (report, emergency route step 2). */
class AcknowledgeAlertUseCase @Inject constructor(
    private val repository: AlertRepository
) {
    suspend operator fun invoke(alertId: String, userId: String): Result<Alert> =
        repository.acknowledgeAlert(alertId, userId)
}
