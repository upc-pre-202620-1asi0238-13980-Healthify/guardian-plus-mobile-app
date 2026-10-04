package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertRepository
import javax.inject.Inject

class GetAlertByIdUseCase @Inject constructor(
    private val repository: AlertRepository
) {
    suspend operator fun invoke(alertId: String): Result<Alert> = repository.getAlertById(alertId)
}
