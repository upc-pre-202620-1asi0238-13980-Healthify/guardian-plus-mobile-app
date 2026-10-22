package com.example.guardian_plus_mobile_app.features.profile.domain.model

import java.time.LocalDate

/** Descriptive profile of a Care Circle member, owned by Profile and linked to IAM's account by [userId]. */
data class UserProfile(
    val id: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
) {
    val fullName: String get() = "$firstName $lastName"
}

/** The person under care. */
data class CareRecipient(
    val id: String,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate
) {
    val fullName: String get() = "$firstName $lastName"
}

/** What ties a member to a person under care; only an active one gives access to that person's data. */
data class CareRelationship(
    val id: String,
    val userId: String,
    val careRecipientProfileId: String,
    val relationshipType: RelationshipType,
    val active: Boolean
)

/** Same values as the platform's RelationshipType. */
enum class RelationshipType {
    FAMILY,
    CAREGIVER
}
