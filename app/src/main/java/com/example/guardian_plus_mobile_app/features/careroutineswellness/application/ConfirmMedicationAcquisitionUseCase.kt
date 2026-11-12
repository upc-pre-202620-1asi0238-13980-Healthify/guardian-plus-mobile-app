package com.example.guardian_plus_mobile_app.features.careroutineswellness.application

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStockRepository
import javax.inject.Inject

class ConfirmMedicationAcquisitionUseCase @Inject constructor(
    private val repository: MedicationStockRepository
) {
    /** @param dosesAdded units the family bought; a whole package when null */
    suspend operator fun invoke(medicationStockId: String, dosesAdded: Int? = null): Result<MedicationStock> =
        repository.confirmMedicationAcquisition(medicationStockId, dosesAdded)
}
