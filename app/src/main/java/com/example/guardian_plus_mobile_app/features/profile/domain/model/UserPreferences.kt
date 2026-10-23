package com.example.guardian_plus_mobile_app.features.profile.domain.model

enum class AppLanguage {
    SPANISH_LATIN_AMERICA,
    ENGLISH_UNITED_STATES
}

enum class AppFontScale {
    SMALL,
    DEFAULT,
    LARGE
}

data class UserPreferences(
    val userId: String,
    val language: AppLanguage,
    val notificationsEnabled: Boolean,
    val highContrastEnabled: Boolean,
    val reduceMotionEnabled: Boolean,
    val fontScale: AppFontScale
)