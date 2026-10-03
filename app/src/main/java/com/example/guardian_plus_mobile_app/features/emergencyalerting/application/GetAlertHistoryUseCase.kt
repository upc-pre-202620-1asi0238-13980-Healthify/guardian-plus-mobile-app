package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertPage
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import java.time.Instant
import javax.inject.Inject

class GetAlertHistoryUseCase @Inject constructor(
    private val repository: AlertRepository
) {
    suspend operator fun invoke(
        careRecipientProfileId: String,
        severity: Severity? = null,
        from: Instant? = null,
        to: Instant? = null,
        page: Int = 0,
        size: Int = DEFAULT_PAGE_SIZE
    ): Result<AlertPage> = repository.getAlertHistory(careRecipientProfileId, severity, from, to, page, size)

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
        // Largest page the platform accepts (AlertsController.MAX_PAGE_SIZE)
        const val MAX_PAGE_SIZE = 100
    }
}
