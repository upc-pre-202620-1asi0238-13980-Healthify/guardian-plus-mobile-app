package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.EmailAvailabilityDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.TwoFactorSettingRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.UserAccountDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** /user-accounts endpoints of the report; placeholders until the platform exposes IAM. */
interface UserAccountService {

    @GET("user-accounts/{userAccountId}")
    suspend fun getUserAccount(@Path("userAccountId") userAccountId: String): Response<UserAccountDto>

    @GET("user-accounts/availability")
    suspend fun getEmailAvailability(@Query("email") email: String): Response<EmailAvailabilityDto>

    @PUT("user-accounts/{userAccountId}/two-factor")
    suspend fun setTwoFactor(
        @Path("userAccountId") userAccountId: String,
        @Body request: TwoFactorSettingRequestDto
    ): Response<UserAccountDto>
}
