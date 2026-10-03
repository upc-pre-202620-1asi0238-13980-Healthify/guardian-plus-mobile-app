package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.simulated

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContextRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import javax.inject.Inject

/** Sample readings, places and wristband status shown with each alert, served locally. */
class SimulatedAlertContextRepository @Inject constructor() : AlertContextRepository {

    override suspend fun getAlertContext(alert: Alert): Result<AlertContext> {
        val context = when (alert.sourceType) {
            AlertSourceType.FALL_DETECTED -> AlertContext(
                locationName = "Dormitorio · Casa de Elena",
                locationUpdatedAgo = "hace 30 s",
                heartRateBpm = 112,
                oxygenSaturation = 95
            )
            AlertSourceType.SOS_TRIGGERED -> AlertContext(
                locationName = "Parque Kennedy · Miraflores",
                locationUpdatedAgo = "hace 10 s",
                heartRateBpm = 104,
                oxygenSaturation = 97,
                deviceSummary = "Conectada · Batería 64%"
            )
            AlertSourceType.VITAL_SIGN_ANOMALY -> AlertContext(
                readingSummary = "124 lpm · 3 lecturas fuera de rango",
                heartRateBpm = 124,
                oxygenSaturation = 96
            )
            AlertSourceType.SAFE_ZONE_VIOLATION -> AlertContext(
                locationName = "Av. Larco · Miraflores",
                locationUpdatedAgo = "hace 1 min"
            )
            AlertSourceType.PROLONGED_INACTIVITY -> AlertContext(
                readingSummary = "Sin movimiento por 2 h",
                locationName = "Sala · Casa de Elena",
                locationUpdatedAgo = "hace 2 min"
            )
            AlertSourceType.REMINDER_REISSUED -> AlertContext(readingSummary = "Losartán 50 mg · 14:00")
            AlertSourceType.MEDICATION_RESTOCK_SUGGESTED -> AlertContext(readingSummary = "Losartán 50 mg · quedan 2 días")
        }
        return Result.success(context)
    }
}
