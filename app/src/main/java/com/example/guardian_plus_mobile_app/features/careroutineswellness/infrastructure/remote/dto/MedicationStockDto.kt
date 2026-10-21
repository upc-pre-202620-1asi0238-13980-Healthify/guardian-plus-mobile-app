package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto

// Same shape as the platform's MedicationStockResource
data class MedicationStockDto(
    val id: String,
    val personUnderCareId: String,
    val medicationName: String,
    val dosage: String?,
    val remainingDoses: Int,
    val dailyConsumption: Double,
    val packageSize: Int,
    val lastAcquisitionDate: String?,
    val remainingDaysOfSupply: Double,
    val estimatedDepletionDate: String?,
    val restockRecommended: Boolean
)

// Same body as the platform's ConfirmMedicationAcquisitionResource; without dosesAdded a whole package is added
data class ConfirmMedicationAcquisitionRequestDto(
    val dosesAdded: Int? = null
)
