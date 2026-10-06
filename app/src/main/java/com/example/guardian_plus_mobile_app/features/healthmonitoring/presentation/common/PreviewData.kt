package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSign
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.ReadingClassification
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import java.time.Instant

// Fake readings for the @Preview functions of the health screens (the Figma values)

internal val previewNow: Instant = Instant.parse("2026-10-06T14:33:00Z")

internal fun previewLiveVitals(heartRate: Double = 78.0): LiveVitalSigns {
    fun reading(type: VitalSignType, value: Double, min: Double, max: Double) = LiveVitalSign(
        id = type.name,
        type = type,
        typeName = type.name,
        unit = "",
        value = value,
        measuredAt = previewNow.minusSeconds(60),
        normalMinimum = min,
        normalMaximum = max,
        classification = when {
            value < min -> ReadingClassification.BELOW_RANGE
            value > max -> ReadingClassification.ABOVE_RANGE
            else -> ReadingClassification.WITHIN_RANGE
        },
        liveSignal = true
    )
    return LiveVitalSigns(
        careRecipientProfileId = "elena",
        retrievedAt = previewNow.minusSeconds(120),
        vitalSigns = listOf(
            reading(VitalSignType.HR, heartRate, 60.0, 100.0),
            reading(VitalSignType.BP_SYS, 118.0, 90.0, 140.0),
            reading(VitalSignType.BP_DIA, 76.0, 60.0, 90.0),
            reading(VitalSignType.SPO2, 98.0, 92.0, 100.0),
            reading(VitalSignType.TEMP, 36.6, 36.0, 37.5),
            reading(VitalSignType.RESP_RATE, 16.0, 12.0, 20.0)
        )
    )
}
