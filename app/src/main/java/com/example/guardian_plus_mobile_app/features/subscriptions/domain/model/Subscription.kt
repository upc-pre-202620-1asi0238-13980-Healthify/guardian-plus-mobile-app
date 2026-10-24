package com.example.guardian_plus_mobile_app.features.subscriptions.domain.model

data class Subscription(
    val id: String,
    val subscriberId: String,
    val planId: String,
    val status: String,
    val currentPeriodStart: String?,
    val currentPeriodEnd: String?,
    val cancelAtPeriodEnd: Boolean
)