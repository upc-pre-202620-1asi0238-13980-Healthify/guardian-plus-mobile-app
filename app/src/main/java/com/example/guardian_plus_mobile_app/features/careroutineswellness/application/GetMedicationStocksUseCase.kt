package com.example.guardian_plus_mobile_app.features.careroutineswellness.application

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStockRepository
import javax.inject.Inject

class GetMedicationStocksUseCase @Inject constructor(
    private val repository: MedicationStockRepository
) {
    suspend operator fun invoke(personUnderCareId: String): Result<List<MedicationStock>> =
        repository.getMedicationStocks(personUnderCareId)
}
