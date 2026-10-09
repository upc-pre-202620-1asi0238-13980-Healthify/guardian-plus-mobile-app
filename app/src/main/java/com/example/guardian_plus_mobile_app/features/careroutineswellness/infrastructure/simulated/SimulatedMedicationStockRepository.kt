package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.simulated

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStockRepository
import javax.inject.Inject

/** Sample medication stocks, served locally until the app reads /medication-stocks from the platform. */
class SimulatedMedicationStockRepository @Inject constructor() : MedicationStockRepository {

    override suspend fun getMedicationStocks(personUnderCareId: String): Result<List<MedicationStock>> =
        Result.success(
            listOf(
                MedicationStock(
                    id = "stock-losartan",
                    medicationName = "Losartán",
                    dosage = "50 mg",
                    remainingDoses = 6,
                    dailyConsumption = 2.0,
                    remainingDaysOfSupply = 3.0,
                    restockRecommended = true
                ),
                MedicationStock(
                    id = "stock-metformina",
                    medicationName = "Metformina",
                    dosage = "850 mg",
                    remainingDoses = 38,
                    dailyConsumption = 2.0,
                    remainingDaysOfSupply = 19.0,
                    restockRecommended = false
                )
            )
        )
}
