package com.example.guardian_plus_mobile_app.features.profile.presentation.language

import com.example.guardian_plus_mobile_app.features.profile.domain.model.AppFontScale
import com.example.guardian_plus_mobile_app.features.profile.domain.model.AppLanguage

data class LanguageUiState(
    val language: AppLanguage =
        AppLanguage.SPANISH_LATIN_AMERICA,

    val highContrastEnabled: Boolean = false,
    val reduceMotionEnabled: Boolean = false,

    val fontScale: AppFontScale =
        AppFontScale.DEFAULT,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)