package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto

// Same fields as the platform's UserAccountResource; it never carries the password hash
data class UserAccountDto(
    val id: String,
    val email: String,
    val status: String,
    val emailVerified: Boolean,
    val twoFactorEnabled: Boolean
)

data class EmailAvailabilityDto(
    val email: String,
    val available: Boolean
)

data class TwoFactorSettingRequestDto(
    val enabled: Boolean
)
