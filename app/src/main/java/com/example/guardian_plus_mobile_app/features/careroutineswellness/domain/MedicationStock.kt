package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

/** Doses left of one medication and how long they last at the current consumption. */
data class MedicationStock(
    val id: String,
    val medicationName: String,
    val dosage: String?,
    val remainingDoses: Int,
    val dailyConsumption: Double,
    val remainingDaysOfSupply: Double,
    val restockRecommended: Boolean
)
