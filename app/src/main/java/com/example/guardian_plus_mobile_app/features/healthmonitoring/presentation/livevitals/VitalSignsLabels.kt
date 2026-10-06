package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation




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
