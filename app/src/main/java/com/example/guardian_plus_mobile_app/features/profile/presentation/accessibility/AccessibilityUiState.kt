package com.example.guardian_plus_mobile_app.features.profile.presentation.accessibility

import com.example.guardian_plus_mobile_app.features.profile.domain.model.AppFontScale
import com.example.guardian_plus_mobile_app.features.profile.domain.model.AppLanguage

data class AccessibilityUiState(
    val language: AppLanguage =
        AppLanguage.SPANISH_LATIN_AMERICA,

    val fontScale: AppFontScale =
        AppFontScale.DEFAULT,

    val highContrastEnabled: Boolean = false,
    val reduceMotionEnabled: Boolean = false,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)