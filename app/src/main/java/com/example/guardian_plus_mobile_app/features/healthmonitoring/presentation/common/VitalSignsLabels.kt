package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common


import com.example.guardian_plus_mobile_app.R

import androidx.annotation.DrawableRes
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType


// Literally just translations

// The backend sends English names; the app speaks Spanish
val VitalSignType.label: String
    get() = when (this) {
        VitalSignType.HR -> "Ritmo cardíaco"
        VitalSignType.BP_SYS, VitalSignType.BP_DIA -> "Presión arterial"
        VitalSignType.SPO2 -> "Saturación"
        VitalSignType.TEMP -> "Temperatura"
        VitalSignType.RESP_RATE -> "Respiración"
    }

val VitalSignType.displayUnit: String
    get() = when (this) {
        VitalSignType.HR -> "lpm"
        VitalSignType.BP_SYS, VitalSignType.BP_DIA -> "mmHg"
        VitalSignType.SPO2 -> "%"
        VitalSignType.TEMP -> "°C"
        VitalSignType.RESP_RATE -> "rpm"
    }



// Chip text of the "Historial" filter row
val VitalSignType.shortLabel: String
    get() = when (this) {
        VitalSignType.HR -> "Ritmo"
        VitalSignType.BP_SYS, VitalSignType.BP_DIA -> "Presión"
        VitalSignType.SPO2 -> "SpO₂"
        VitalSignType.TEMP -> "Temp"
        VitalSignType.RESP_RATE -> "Respir"
    }


@DrawableRes
fun VitalSignType.iconRes(): Int = when (this) {
    VitalSignType.HR -> R.drawable.ic_heart
    VitalSignType.BP_SYS, VitalSignType.BP_DIA -> R.drawable.ic_bar_chart_2
    VitalSignType.SPO2 -> R.drawable.ic_activity
    VitalSignType.TEMP -> R.drawable.ic_thermometer
    VitalSignType.RESP_RATE -> R.drawable.ic_wind
}
