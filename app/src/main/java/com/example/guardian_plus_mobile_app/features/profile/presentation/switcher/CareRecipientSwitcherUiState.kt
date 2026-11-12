package com.example.guardian_plus_mobile_app.features.profile.presentation.switcher

import com.example.guardian_plus_mobile_app.features.profile.domain.model.CareRecipient

data class CareRecipientSwitcherUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // Everyone the account cares for, in the order they were added
    val careRecipients: List<CareRecipient> = emptyList(),
    val currentCareRecipientId: String = "",
    // The row being switched to shows progress and the others wait
    val switchingToId: String? = null,
    // Set once the app shows the chosen person; the sheet closes and the shell reloads the tabs
    val isSwitched: Boolean = false
)
