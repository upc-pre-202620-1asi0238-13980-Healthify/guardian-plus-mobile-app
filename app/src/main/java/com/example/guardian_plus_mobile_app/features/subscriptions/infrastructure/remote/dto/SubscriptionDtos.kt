package com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.remote.dto

data class SubscriptionDto(
    val id: String,
    val subscriberId: String,
    val planId: String,
    val status: String,
    val currentPeriodStart: String?,
    val currentPeriodEnd: String?,
    val cancelAtPeriodEnd: Boolean
)

data class EntitlementDto(
    val code: String,
    val name: String,
    val description: String
)

data class EntitlementSetDto(
    val subscriptionId: String,
    val entitlements: List<EntitlementDto>,
    val updatedAt: String?
)