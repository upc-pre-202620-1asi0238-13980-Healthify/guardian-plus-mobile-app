package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

interface MedicationStockRepository {
    suspend fun getMedicationStocks(personUnderCareId: String): Result<List<MedicationStock>>

    /** "Añadir envase": a whole package is added to the remaining doses. */
    suspend fun confirmMedicationAcquisition(medicationStockId: String): Result<MedicationStock>
}
