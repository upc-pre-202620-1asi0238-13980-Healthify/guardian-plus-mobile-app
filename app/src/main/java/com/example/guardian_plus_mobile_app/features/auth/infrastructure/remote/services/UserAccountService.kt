package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.EmailAvailabilityDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.UserAccountDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** The platform's /user-accounts endpoints. */
interface UserAccountService {

    /** The account the session token belongs to; it needs the token (AuthInterceptor sends it). */
    @GET("user-accounts/me")
    suspend fun getCurrentUserAccount(): Response<UserAccountDto>

    @GET("user-accounts/{userAccountId}")
    suspend fun getUserAccount(@Path("userAccountId") userAccountId: String): Response<UserAccountDto>

    @GET("user-accounts/availability")
    suspend fun getEmailAvailability(@Query("email") email: String): Response<EmailAvailabilityDto>
}
