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
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UpdateContactInformationRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UpdateUserProfileRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UpdateCareRecipientProfileRequestDto
import com.example.guardian_plus_mobile_app.features.profile.domain.model.AppFontScale
import com.example.guardian_plus_mobile_app.features.profile.domain.model.AppLanguage
import com.example.guardian_plus_mobile_app.features.profile.domain.model.UserPreferences
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UpdateLanguageAccessibilityPreferencesRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.services.UserPreferencesService
import java.time.LocalDate
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val userProfileService: UserProfileService,
    private val careRecipientProfileService: CareRecipientProfileService,
    private val careRelationshipService: CareRelationshipService,
    private val userPreferencesService: UserPreferencesService
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

    override suspend fun updateUserProfile(
        userProfileId: String,
        firstName: String,
        lastName: String
    ): Result<UserProfile> =
        apiCall({
            userProfileService.updateUserProfile(
                userProfileId = userProfileId,
                request = UpdateUserProfileRequestDto(
                    firstName = firstName,
                    lastName = lastName
                )
            )
        }) { it.toDomain() }

    override suspend fun updateContactInformation(
        userProfileId: String,
        phoneNumber: String
    ): Result<UserProfile> =
        apiCall({
            userProfileService.updateContactInformation(
                userProfileId = userProfileId,
                request = UpdateContactInformationRequestDto(
                    phoneNumber = phoneNumber
                )
            )
        }) { it.toDomain() }


    override suspend fun getCareRelationships(userId: String): Result<List<CareRelationship>> =
        apiCall({ careRelationshipService.getCareRelationshipsByUserId(userId) }) { relationships ->
            relationships.map { it.toDomain() }
        }

    override suspend fun getCareRelationshipsByCareRecipient(
        careRecipientProfileId: String
    ): Result<List<CareRelationship>> =
        apiCall({
            careRelationshipService.getCareRelationshipsByCareRecipient(
                careRecipientProfileId
            )
        }) { relationships ->
            relationships.map { it.toDomain() }
        }

    override suspend fun getCareRecipient(careRecipientProfileId: String): Result<CareRecipient> =
        apiCall({ careRecipientProfileService.getCareRecipientProfile(careRecipientProfileId) }) { it.toDomain() }

    override suspend fun updateCareRecipient(
        careRecipientProfileId: String,
        firstName: String,
        lastName: String,
        birthDate: LocalDate
    ): Result<CareRecipient> =
        apiCall({
            careRecipientProfileService.updateCareRecipientProfile(
                careRecipientProfileId = careRecipientProfileId,
                request = UpdateCareRecipientProfileRequestDto(
                    firstName = firstName,
                    lastName = lastName,
                    birthDate = birthDate.toString()
                )
            )
        }) { it.toDomain() }

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
    override suspend fun findUserPreferences(
        userId: String
    ): Result<UserPreferences?> =
        apiCall(
            {
                userPreferencesService.getUserPreferences(userId)
            },
            onNotFound = { null }
        ) {
            it.toDomain()
        }

    override suspend fun updateLanguageAndAccessibilityPreferences(
        userId: String,
        language: AppLanguage,
        highContrastEnabled: Boolean,
        reduceMotionEnabled: Boolean,
        fontScale: AppFontScale
    ): Result<UserPreferences> =
        apiCall({
            userPreferencesService.updateLanguageAccessibilityPreferences(
                userId = userId,
                request = UpdateLanguageAccessibilityPreferencesRequestDto(
                    language = language.name,
                    highContrastEnabled = highContrastEnabled,
                    reduceMotionEnabled = reduceMotionEnabled,
                    fontScale = fontScale.name
                )
            )
        }) {
            it.toDomain()
        }
}
