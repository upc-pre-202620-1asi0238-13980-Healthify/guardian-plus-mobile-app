package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

data class EmergencyContact(
    val id: String,
    val careRecipientProfileId: String,
    val userId: String,
    val displayName: String,
    val relationship: String,
    val phoneNumber: String,
    // 1 is the primary contact; the rest are secondary contacts in escalation order
    val priorityOrder: Int,
    val active: Boolean
)
