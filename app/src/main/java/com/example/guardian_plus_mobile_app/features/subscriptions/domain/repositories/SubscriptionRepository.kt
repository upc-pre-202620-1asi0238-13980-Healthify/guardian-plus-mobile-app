package com.example.guardian_plus_mobile_app.features.subscriptions.domain.repositories

import com.example.guardian_plus_mobile_app.features.subscriptions.domain.model.Entitlement
import com.example.guardian_plus_mobile_app.features.subscriptions.domain.model.Subscription

interface SubscriptionRepository {

    suspend fun findCurrentSubscription(
        subscriberId: String
    ): Result<Subscription?>

    suspend fun findEntitlements(
        subscriberId: String
    ): Result<List<Entitlement>>

    suspend fun requestCancellation(
        subscriptionId: String
    ): Result<Subscription>
}