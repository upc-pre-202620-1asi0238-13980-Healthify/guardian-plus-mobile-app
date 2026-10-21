package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStockRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ConfirmMedicationAcquisitionRequestDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.MedicationStockDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.MedicationStockService
import java.time.LocalDate
import javax.inject.Inject

class MedicationStockRepositoryImpl @Inject constructor(
    private val service: MedicationStockService
) : MedicationStockRepository {

    override suspend fun getMedicationStocks(personUnderCareId: String): Result<List<MedicationStock>> =
        apiCall({ service.getMedicationStocks(personUnderCareId) }) { dtos -> dtos.map { it.toDomain() } }

    override suspend fun confirmMedicationAcquisition(medicationStockId: String): Result<MedicationStock> =
        apiCall({ service.confirmMedicationAcquisition(medicationStockId, ConfirmMedicationAcquisitionRequestDto()) }) { it.toDomain() }
}

private fun MedicationStockDto.toDomain() = MedicationStock(
    id = id,
    medicationName = medicationName,
    dosage = dosage,
    remainingDoses = remainingDoses,
    dailyConsumption = dailyConsumption,
    packageSize = packageSize,
    remainingDaysOfSupply = remainingDaysOfSupply,
    estimatedDepletionDate = estimatedDepletionDate?.let(LocalDate::parse),
    restockRecommended = restockRecommended
)
