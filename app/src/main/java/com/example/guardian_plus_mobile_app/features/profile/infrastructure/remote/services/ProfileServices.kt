package com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CareRecipientProfileDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CareRelationshipDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CreateCareRecipientProfileRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CreateUserProfileRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.EstablishCareRelationshipRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UserProfileDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UpdateContactInformationRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UpdateUserProfileRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UpdateCareRecipientProfileRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UpdateLanguageAccessibilityPreferencesRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UserPreferencesDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserProfileService {

    @GET("user-profiles/user/{userId}")
    suspend fun getUserProfileByUserId(@Path("userId") userId: String): Response<UserProfileDto>

    @POST("user-profiles")
    suspend fun createUserProfile(@Body request: CreateUserProfileRequestDto): Response<UserProfileDto>

    @PUT("user-profiles/{userProfileId}")
    suspend fun updateUserProfile(
        @Path("userProfileId") userProfileId: String,
        @Body request: UpdateUserProfileRequestDto
    ): Response<UserProfileDto>

    @PUT("user-profiles/{userProfileId}/contact-information")
    suspend fun updateContactInformation(
        @Path("userProfileId") userProfileId: String,
        @Body request: UpdateContactInformationRequestDto
    ): Response<UserProfileDto>
}

interface CareRecipientProfileService {

    @GET("care-recipient-profiles/{careRecipientProfileId}")
    suspend fun getCareRecipientProfile(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<CareRecipientProfileDto>

    @POST("care-recipient-profiles")
    suspend fun createCareRecipientProfile(
        @Body request: CreateCareRecipientProfileRequestDto
    ): Response<CareRecipientProfileDto>

    @PUT("care-recipient-profiles/{careRecipientProfileId}")
    suspend fun updateCareRecipientProfile(
        @Path("careRecipientProfileId") careRecipientProfileId: String,
        @Body request: UpdateCareRecipientProfileRequestDto
    ): Response<CareRecipientProfileDto>
}

interface CareRelationshipService {

    @GET("care-relationships/user/{userId}")
    suspend fun getCareRelationshipsByUserId(@Path("userId") userId: String): Response<List<CareRelationshipDto>>

    @POST("care-relationships")
    suspend fun establishCareRelationship(@Body request: EstablishCareRelationshipRequestDto): Response<CareRelationshipDto>

    @GET("care-relationships/care-recipient/{careRecipientProfileId}")
    suspend fun getCareRelationshipsByCareRecipient(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<List<CareRelationshipDto>>
}

interface UserPreferencesService {

    @GET("user-preferences/user/{userId}")
    suspend fun getUserPreferences(
        @Path("userId") userId: String
    ): Response<UserPreferencesDto>

    @PUT("user-preferences/user/{userId}/language-accessibility")
    suspend fun updateLanguageAccessibilityPreferences(
        @Path("userId") userId: String,
        @Body request: UpdateLanguageAccessibilityPreferencesRequestDto
    ): Response<UserPreferencesDto>
}
