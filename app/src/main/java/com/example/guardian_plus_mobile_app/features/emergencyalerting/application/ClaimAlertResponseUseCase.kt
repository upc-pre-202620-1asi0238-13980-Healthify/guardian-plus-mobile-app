package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertRepository
import javax.inject.Inject

/** "Voy en camino": tells the rest of the Care Circle who is going (US25). It also acknowledges the alert. */
class ClaimAlertResponseUseCase @Inject constructor(
    private val repository: AlertRepository
) {
    suspend operator fun invoke(alertId: String, responderUserId: String): Result<Alert> =
        repository.claimAlertResponse(alertId, responderUserId)
}
