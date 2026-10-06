package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto

data class LiveVitalSignDto(
    val vitalSignId: String,
    val vitalSignType: String,
    val vitalSignTypeName: String,
    val unit: String,
    val value: Double,
    val measuredAt: String,
    val normalMinimum: Double,
    val normalMaximum: Double,
    val classification: String,
    val liveSignal: Boolean
)
