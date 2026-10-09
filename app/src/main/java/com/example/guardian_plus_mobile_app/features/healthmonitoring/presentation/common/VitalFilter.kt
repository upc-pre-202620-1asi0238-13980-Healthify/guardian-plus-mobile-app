package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common

import androidx.annotation.StringRes
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType

// Blood pressure is one tile, one chip and one chart for both halves, so BP_DIA is never listed on its own
val displayedVitalTypes = listOf(
    VitalSignType.HR,
    VitalSignType.BP_SYS,
    VitalSignType.SPO2,
    VitalSignType.TEMP,
    VitalSignType.RESP_RATE
)

/** How far back "Historial" looks. */
enum class HistoryPeriod(val dayCount: Int) {
    DAY(1),
    WEEK(7),
    MONTH(30)
}

/** One row of "Buscar y filtrar", in the order of the prototype. */
enum class VitalFilterOption(@param:StringRes val labelRes: Int) {
    HEART_RATE(R.string.filter_heart_rate),
    BLOOD_PRESSURE(R.string.filter_blood_pressure),
    OXYGEN_SATURATION(R.string.filter_oxygen_saturation),
    TEMPERATURE(R.string.filter_temperature),
    RESPIRATION(R.string.filter_respiration),
    DAY(R.string.filter_day),
    WEEK(R.string.filter_week),
    MONTH(R.string.filter_month),
    NORMAL(R.string.filter_normal),
    OBSERVATION(R.string.filter_observation);

    val type: VitalSignType?
        get() = when (this) {
            HEART_RATE -> VitalSignType.HR
            BLOOD_PRESSURE -> VitalSignType.BP_SYS
            OXYGEN_SATURATION -> VitalSignType.SPO2
            TEMPERATURE -> VitalSignType.TEMP
            RESPIRATION -> VitalSignType.RESP_RATE
            else -> null
        }

    val period: HistoryPeriod?
        get() = when (this) {
            DAY -> HistoryPeriod.DAY
            WEEK -> HistoryPeriod.WEEK
            MONTH -> HistoryPeriod.MONTH
            else -> null
        }
}

/**
 * What "Buscar y filtrar" has selected. Within a group nothing selected means no restriction, so the empty
 * filter shows every vital sign of the last week, whatever its state.
 */
data class VitalFilter(val options: Set<VitalFilterOption> = emptySet()) {

    val types: List<VitalSignType>
        get() = options.mapNotNull { it.type }.ifEmpty { displayedVitalTypes }.sortedBy { displayedVitalTypes.indexOf(it) }

    // The periods exclude each other, see toggle
    val period: HistoryPeriod get() = options.firstNotNullOfOrNull { it.period } ?: HistoryPeriod.WEEK

    val isEmpty: Boolean get() = options.isEmpty()

    fun includes(type: VitalSignType): Boolean =
        (if (type == VitalSignType.BP_DIA) VitalSignType.BP_SYS else type) in types

    fun includesState(withinRange: Boolean): Boolean {
        val normal = VitalFilterOption.NORMAL in options
        val observation = VitalFilterOption.OBSERVATION in options
        return (!normal && !observation) || (withinRange && normal) || (!withinRange && observation)
    }

    // A history has one period, so picking one replaces the other two
    fun toggle(option: VitalFilterOption): VitalFilter = when {
        option in options -> VitalFilter(options - option)
        option.period != null -> VitalFilter(options.filter { it.period == null }.toSet() + option)
        else -> VitalFilter(options + option)
    }

    companion object {
        // rememberSaveable keeps the names, enums are not Bundle-friendly as a set
        fun fromNames(names: List<String>): VitalFilter =
            VitalFilter(names.mapNotNull { name -> VitalFilterOption.entries.firstOrNull { it.name == name } }.toSet())
    }
}
