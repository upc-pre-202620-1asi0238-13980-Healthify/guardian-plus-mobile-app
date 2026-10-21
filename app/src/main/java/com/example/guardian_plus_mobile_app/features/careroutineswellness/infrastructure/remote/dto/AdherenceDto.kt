package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto

data class AdherenceDto(
    val type: String,
    val from: String,
    val to: String,
    val due: Int,
    val confirmed: Int,
    val percentage: Int,
    val days: List<DailyAdherenceDto>?
)

data class DailyAdherenceDto(
    val date: String,
    val due: Int,
    val confirmed: Int,
    val percentage: Int
)
