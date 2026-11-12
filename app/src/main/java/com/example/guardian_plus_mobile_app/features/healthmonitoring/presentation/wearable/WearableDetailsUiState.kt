package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.wearable

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice

data class WearableDetailsUiState(
    val careRecipientName: String = "",
    val wearable: WearableDevice? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // The button shows progress and cannot be pressed twice while the platform unlinks the device
    val isUnlinking: Boolean = false,
    // One-shot message for the screen: the device was unlinked, or why it could not be
    val actionMessage: String? = null
)