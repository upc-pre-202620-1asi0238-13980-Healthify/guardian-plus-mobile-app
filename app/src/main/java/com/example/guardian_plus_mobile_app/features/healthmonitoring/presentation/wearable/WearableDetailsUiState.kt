package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.wearable

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice

data class WearableDetailsUiState(
    val careRecipientName: String = "",
    val wearable: WearableDevice? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)