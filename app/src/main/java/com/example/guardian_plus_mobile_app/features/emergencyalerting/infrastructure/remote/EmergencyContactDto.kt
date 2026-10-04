package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

data class EmergencyContactDto(
    val id: String,
    val careRecipientProfileId: String,
    val userId: String,
    val displayName: String,
    val relationship: String,
    val phoneNumber: String,
    val priorityOrder: Int,
    val active: Boolean
)
