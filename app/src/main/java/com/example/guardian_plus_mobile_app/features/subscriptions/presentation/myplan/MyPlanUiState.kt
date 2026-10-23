package com.example.guardian_plus_mobile_app.features.subscriptions.presentation.myplan

data class MyPlanUiState(
    val planName: String = "",
    val statusLabel: String = "",
    val description: String = "",
    val benefits: List<String> = emptyList(),
    val billingCycle: String = "",
    val automaticRenewal: Boolean = false,
    val hasSubscription: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)