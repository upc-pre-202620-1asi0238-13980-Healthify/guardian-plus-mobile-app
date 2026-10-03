package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

data class AddEmergencyContactRequestDto(
    val careRecipientProfileId: String,
    val userId: String,
    val displayName: String,
    val relationship: String,
    val phoneNumber: String
)
