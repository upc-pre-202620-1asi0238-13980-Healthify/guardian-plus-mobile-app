package com.example.guardian_plus_mobile_app.features.profile.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.profile.domain.model.CareRecipient
import com.example.guardian_plus_mobile_app.features.profile.domain.model.CareRelationship
import com.example.guardian_plus_mobile_app.features.profile.domain.model.RelationshipType
import com.example.guardian_plus_mobile_app.features.profile.domain.model.UserProfile
import com.example.guardian_plus_mobile_app.features.profile.domain.repositories.ProfileRepository
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CreateCareRecipientProfileRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CreateUserProfileRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.EstablishCareRelationshipRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.services.CareRecipientProfileService
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.services.CareRelationshipService
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.services.UserProfileService
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.toDomain
import java.time.LocalDate
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val userProfileService: UserProfileService,
    private val careRecipientProfileService: CareRecipientProfileService,
    private val careRelationshipService: CareRelationshipService
) : ProfileRepository {

    // A member without a profile yet is an expected answer (404), not an error
    override suspend fun findUserProfile(userId: String): Result<UserProfile?> =
        apiCall({ userProfileService.getUserProfileByUserId(userId) }, onNotFound = { null }) { it.toDomain() }

    override suspend fun createUserProfile(
        userId: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Result<UserProfile> =
        apiCall({
            userProfileService.createUserProfile(CreateUserProfileRequestDto(userId, firstName, lastName, phoneNumber))
        }) { it.toDomain() }

    override suspend fun getCareRelationships(userId: String): Result<List<CareRelationship>> =
        apiCall({ careRelationshipService.getCareRelationshipsByUserId(userId) }) { relationships ->
            relationships.map { it.toDomain() }
        }

    override suspend fun getCareRecipient(careRecipientProfileId: String): Result<CareRecipient> =
        apiCall({ careRecipientProfileService.getCareRecipientProfile(careRecipientProfileId) }) { it.toDomain() }

    override suspend fun createCareRecipient(
        createdByUserId: String,
        firstName: String,
        lastName: String,
        birthDate: LocalDate
    ): Result<CareRecipient> =
        apiCall({
            careRecipientProfileService.createCareRecipientProfile(
                CreateCareRecipientProfileRequestDto(createdByUserId, firstName, lastName, birthDate.toString())
            )
        }) { it.toDomain() }

    override suspend fun establishCareRelationship(
        userId: String,
        careRecipientProfileId: String,
        relationshipType: RelationshipType
    ): Result<CareRelationship> =
        apiCall({
            careRelationshipService.establishCareRelationship(
                EstablishCareRelationshipRequestDto(userId, careRecipientProfileId, relationshipType.name)
            )
        }) { it.toDomain() }
}
