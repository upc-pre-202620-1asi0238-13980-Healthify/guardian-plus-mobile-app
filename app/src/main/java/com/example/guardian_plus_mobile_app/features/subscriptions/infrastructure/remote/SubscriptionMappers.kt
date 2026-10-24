package com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.subscriptions.domain.model.Entitlement
import com.example.guardian_plus_mobile_app.features.subscriptions.domain.model.Subscription
import com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.remote.dto.EntitlementDto
import com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.remote.dto.SubscriptionDto

fun SubscriptionDto.toDomain() = Subscription(
    id = id,
    subscriberId = subscriberId,
    planId = planId,
    status = status,
    currentPeriodStart = currentPeriodStart,
    currentPeriodEnd = currentPeriodEnd,
    cancelAtPeriodEnd = cancelAtPeriodEnd
)

fun EntitlementDto.toDomain() = Entitlement(
    code = code,
    name = name,
    description = description
)