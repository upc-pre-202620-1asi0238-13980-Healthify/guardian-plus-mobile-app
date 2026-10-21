package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.LocalDate

/** Doses left of one medication and how long they last at the current consumption. */
data class MedicationStock(
    val id: String,
    val medicationName: String,
    val dosage: String?,
    val remainingDoses: Int,
    val dailyConsumption: Double,
    // Doses added by each "Añadir envase"
    val packageSize: Int,
    val remainingDaysOfSupply: Double,
    val estimatedDepletionDate: LocalDate?,
    val restockRecommended: Boolean
) {
    /** Remaining doses against a full package, capped at 100 % after several packages. */
    val packagePercentage: Int
        get() = if (packageSize <= 0) 0 else (remainingDoses * 100 / packageSize).coerceIn(0, 100)
}
