package com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.subscriptions.domain.model.Entitlement
import com.example.guardian_plus_mobile_app.features.subscriptions.domain.model.Subscription
import com.example.guardian_plus_mobile_app.features.subscriptions.domain.repositories.SubscriptionRepository
import com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.remote.services.SubscriptionService
import com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.remote.toDomain
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val service: SubscriptionService
) : SubscriptionRepository {

    override suspend fun findCurrentSubscription(
        subscriberId: String
    ): Result<Subscription?> =
        apiCall(
            request = {
                service.getCurrentSubscription(subscriberId)
            },
            onNotFound = { null }
        ) {
            it.toDomain()
        }

    override suspend fun findEntitlements(
        subscriberId: String
    ): Result<List<Entitlement>> =
        apiCall(
            request = {
                service.getEntitlements(subscriberId)
            },
            onNotFound = { emptyList() }
        ) { response ->
            response.entitlements.map {
                it.toDomain()
            }
        }

    override suspend fun requestCancellation(
        subscriptionId: String
    ): Result<Subscription> =
        apiCall(
            request = {
                service.requestCancellation(subscriptionId)
            }
        ) {
            it.toDomain()
        }
}