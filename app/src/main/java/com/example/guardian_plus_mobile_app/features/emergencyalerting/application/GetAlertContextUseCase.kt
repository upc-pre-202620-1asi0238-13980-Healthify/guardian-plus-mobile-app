package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContextRepository
import javax.inject.Inject

class GetAlertContextUseCase @Inject constructor(
    private val repository: AlertContextRepository
) {
    suspend operator fun invoke(alert: Alert): Result<AlertContext> = repository.getAlertContext(alert)
}
