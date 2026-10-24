package com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.remote.dto.EntitlementSetDto
import com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.remote.dto.SubscriptionDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SubscriptionService {

    @GET("subscriptions/subscriber/{subscriberId}/status")
    suspend fun getCurrentSubscription(
        @Path("subscriberId") subscriberId: String
    ): Response<SubscriptionDto>

    @GET("subscriptions/subscriber/{subscriberId}/entitlements")
    suspend fun getEntitlements(
        @Path("subscriberId") subscriberId: String
    ): Response<EntitlementSetDto>

    @POST("subscriptions/{subscriptionId}/cancellation")
    suspend fun requestCancellation(
        @Path("subscriptionId") subscriptionId: String
    ): Response<SubscriptionDto>
}