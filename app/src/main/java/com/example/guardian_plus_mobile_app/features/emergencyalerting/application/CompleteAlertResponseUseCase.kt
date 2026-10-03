package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertRepository
import javax.inject.Inject

/** "Llegué": the member who was on the way reached the person under care. */
class CompleteAlertResponseUseCase @Inject constructor(
    private val repository: AlertRepository
) {
    suspend operator fun invoke(alertId: String, responseId: String, notes: String?): Result<Alert> =
        repository.completeAlertResponse(alertId, responseId, notes)
}
