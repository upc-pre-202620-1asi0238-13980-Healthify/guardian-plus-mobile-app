package com.example.guardian_plus_mobile_app.features.subscriptions.presentation.myplan

data class MyPlanUiState(
    val subscriptionId: String? = null,
    val planName: String = "",
    val statusLabel: String = "",
    val description: String = "",
    val benefits: List<String> = emptyList(),
    val billingCycle: String = "",
    val automaticRenewal: Boolean = false,
    val hasSubscription: Boolean = false,
    val isLoading: Boolean = false,
    val isCancelling: Boolean = false,
    val cancellationMessage: String? = null,
    val cancellationError: String? = null,
    val errorMessage: String? = null
)