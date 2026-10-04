package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertRepository
import javax.inject.Inject

/** Alerts that were sent to this Care Circle member and still wait for them to acknowledge. */
class GetPendingAlertsUseCase @Inject constructor(
    private val repository: AlertRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Alert>> = repository.getPendingAlerts(userId)
}
