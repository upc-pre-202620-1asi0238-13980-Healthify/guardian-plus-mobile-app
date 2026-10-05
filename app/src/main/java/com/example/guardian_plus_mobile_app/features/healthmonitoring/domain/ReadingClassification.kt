package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain


enum class ReadingClassification {
    BELOW_RANGE,
    WITHIN_RANGE,
    ABOVE_RANGE;

    val isOutRange: Boolean get() = this != WITHIN_RANGE
}

