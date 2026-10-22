package com.example.guardian_plus_mobile_app.features.profile.domain.repositories

import com.example.guardian_plus_mobile_app.features.profile.domain.model.CareRecipient
import com.example.guardian_plus_mobile_app.features.profile.domain.model.CareRelationship
import com.example.guardian_plus_mobile_app.features.profile.domain.model.RelationshipType
import com.example.guardian_plus_mobile_app.features.profile.domain.model.UserProfile
import java.time.LocalDate

interface ProfileRepository {
    /** The member's profile, or null while it has not been created. */
    suspend fun findUserProfile(userId: String): Result<UserProfile?>

    suspend fun createUserProfile(userId: String, firstName: String, lastName: String, phoneNumber: String): Result<UserProfile>

    suspend fun getCareRelationships(userId: String): Result<List<CareRelationship>>

    suspend fun getCareRecipient(careRecipientProfileId: String): Result<CareRecipient>

    suspend fun createCareRecipient(createdByUserId: String, firstName: String, lastName: String, birthDate: LocalDate): Result<CareRecipient>

    suspend fun establishCareRelationship(
        userId: String,
        careRecipientProfileId: String,
        relationshipType: RelationshipType
    ): Result<CareRelationship>
}
