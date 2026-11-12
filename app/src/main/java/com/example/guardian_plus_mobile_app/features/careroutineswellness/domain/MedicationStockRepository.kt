package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

interface MedicationStockRepository {
    suspend fun getMedicationStocks(personUnderCareId: String): Result<List<MedicationStock>>

    /** "Añadir envase": [dosesAdded] units are added to the remaining doses, a whole package when null. */
    suspend fun confirmMedicationAcquisition(medicationStockId: String, dosesAdded: Int? = null): Result<MedicationStock>
}
