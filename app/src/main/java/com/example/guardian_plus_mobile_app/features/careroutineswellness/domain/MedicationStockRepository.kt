package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

interface MedicationStockRepository {
    suspend fun getMedicationStocks(personUnderCareId: String): Result<List<MedicationStock>>
}
