package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import java.time.Instant
import kotlin.math.roundToInt



//here we hold all the states the screen we are working on well need, like loading, error popup, some other domain
//related stuff aswell
data class LiveVitalsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val careRecipientFirstName: String = "",
    val vitals: LiveVitalSigns? = null,
    val hasWristband: Boolean = false,
    // Server time of the last refresh; "Hace 2 min" is measured against it, never the phone clock
    val now: Instant = Instant.EPOCH
) {
    val allWithinRange: Boolean get() = vitals?.allWithinRange ?: false

    val hasLiveSignal: Boolean get() = vitals?.hasLiveSignal ?: false

    // The backend stores systolic and diastolic as two readings; the screen shows them as one
    val bloodPressureText: String?
        get() {
            val systolic = vitals?.get(VitalSignType.BP_SYS) ?: return null
            val diastolic = vitals[VitalSignType.BP_DIA] ?: return null
            return "${systolic.value.roundToInt()}/${diastolic.value.roundToInt()}"
        }

    val bloodPressureOutOfRange: Boolean
        get() = vitals?.get(VitalSignType.BP_SYS)?.classification?.isOutRange == true ||
            vitals?.get(VitalSignType.BP_DIA)?.classification?.isOutRange == true
}
