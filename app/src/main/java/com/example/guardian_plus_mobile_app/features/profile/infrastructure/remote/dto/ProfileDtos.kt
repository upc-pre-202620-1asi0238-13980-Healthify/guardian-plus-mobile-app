package com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto

// Bodies of the platform's Profile controllers (/user-profiles, /care-recipient-profiles, /care-relationships)
data class UserProfileDto(
    val id: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)

data class CreateUserProfileRequestDto(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)

// birthDate is an ISO-8601 date (yyyy-MM-dd)
data class CareRecipientProfileDto(
    val id: String,
    val createdByUserId: String,
    val firstName: String,
    val lastName: String,
    val birthDate: String
)

data class CreateCareRecipientProfileRequestDto(
    val createdByUserId: String,
    val firstName: String,
    val lastName: String,
    val birthDate: String
)

data class CareRelationshipDto(
    val id: String,
    val userId: String,
    val careRecipientProfileId: String,
    val relationshipType: String,
    val status: String
)

data class EstablishCareRelationshipRequestDto(
    val userId: String,
    val careRecipientProfileId: String,
    val relationshipType: String
)
