package com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CareRecipientProfileDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CareRelationshipDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CreateCareRecipientProfileRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CreateUserProfileRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.EstablishCareRelationshipRequestDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UserProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserProfileService {

    @GET("user-profiles/user/{userId}")
    suspend fun getUserProfileByUserId(@Path("userId") userId: String): Response<UserProfileDto>

    @POST("user-profiles")
    suspend fun createUserProfile(@Body request: CreateUserProfileRequestDto): Response<UserProfileDto>
}

interface CareRecipientProfileService {

    @GET("care-recipient-profiles/{careRecipientProfileId}")
    suspend fun getCareRecipientProfile(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<CareRecipientProfileDto>

    @POST("care-recipient-profiles")
    suspend fun createCareRecipientProfile(@Body request: CreateCareRecipientProfileRequestDto): Response<CareRecipientProfileDto>
}

interface CareRelationshipService {

    @GET("care-relationships/user/{userId}")
    suspend fun getCareRelationshipsByUserId(@Path("userId") userId: String): Response<List<CareRelationshipDto>>

    @POST("care-relationships")
    suspend fun establishCareRelationship(@Body request: EstablishCareRelationshipRequestDto): Response<CareRelationshipDto>
}
