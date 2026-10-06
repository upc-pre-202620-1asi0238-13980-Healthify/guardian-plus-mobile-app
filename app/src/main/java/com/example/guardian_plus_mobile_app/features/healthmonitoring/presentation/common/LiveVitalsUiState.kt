package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns



//here we hold all the states the screen we are working on well need, like loading, error popup, some other domain
//related stuff aswell
data class LiveVitalsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val careRecipientFirstName: String = "",
    val vitals: LiveVitalSigns? = null,
    val hasWristband: Boolean = false
) {
    val allWithinRange: Boolean get() = vitals?.allWithinRange ?: false

    val hasLiveSignal: Boolean get() = vitals?.hasLiveSignal ?: false
}

