package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport

import androidx.annotation.StringRes
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReportType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.StabilityIndex
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignSummary
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.displayedVitalTypes
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.format
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Shared by the report screen and its PDF, so both read the same

private val spanish = Locale.forLanguageTag("es-PE")
private val dayMonth = DateTimeFormatter.ofPattern("d MMM", spanish)
private val dayMonthYear = DateTimeFormatter.ofPattern("d MMM yyyy", spanish)
private val dateTime = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", spanish)

/** "3 – 9 oct 2026", "29 oct – 4 nov 2026", or "9 oct 2026" for a one-day report. */
fun HealthReport.periodText(): String = when {
    periodStart == periodEnd -> periodEnd.format(dayMonthYear)
    periodStart.year == periodEnd.year && periodStart.month == periodEnd.month ->
        "${periodStart.dayOfMonth} – ${periodEnd.format(dayMonthYear)}"
    periodStart.year == periodEnd.year -> "${periodStart.format(dayMonth)} – ${periodEnd.format(dayMonthYear)}"
    else -> "${periodStart.format(dayMonthYear)} – ${periodEnd.format(dayMonthYear)}"
}.cleanAbbreviations()

fun HealthReport.generatedAtText(zone: ZoneId = ZoneId.systemDefault()): String =
    generatedAt.atZone(zone).format(dateTime).cleanAbbreviations()

fun LocalDate.shortText(): String = format(dayMonthYear).cleanAbbreviations()

// Some JDKs abbreviate Spanish months with a dot ("oct.") and others do not; the prototype has none
private fun String.cleanAbbreviations(): String = replace(".", "")

@get:StringRes
val HealthReportType.labelRes: Int
    get() = when (this) {
        HealthReportType.ON_DEMAND -> R.string.report_type_on_demand
        HealthReportType.WEEKLY_AUTOMATIC -> R.string.report_type_weekly
    }

@get:StringRes
val StabilityIndex.labelRes: Int
    get() = when (this) {
        StabilityIndex.STABLE -> R.string.report_stability_stable
        StabilityIndex.UNSTABLE -> R.string.report_stability_unstable
        StabilityIndex.RECURRENT -> R.string.report_stability_recurrent
    }

/** One sign of a report as it is shown: blood pressure joins its two halves into "118/76". */
data class SummaryRow(
    val type: VitalSignType,
    val averageText: String,
    val minText: String,
    val maxText: String,
    val readingsCount: Int,
    val outOfRangeCount: Int,
    val stability: StabilityIndex
)

fun HealthReport.summaryRows(): List<SummaryRow> = displayedVitalTypes.mapNotNull { type ->
    val summary = this[type] ?: return@mapNotNull null
    val diastolic = if (type == VitalSignType.BP_SYS) this[VitalSignType.BP_DIA] else null
    if (diastolic == null) {
        summary.toRow()
    } else {
        SummaryRow(
            type = type,
            averageText = "${type.format(summary.averageValue)}/${type.format(diastolic.averageValue)}",
            minText = "${type.format(summary.minValue)}/${type.format(diastolic.minValue)}",
            maxText = "${type.format(summary.maxValue)}/${type.format(diastolic.maxValue)}",
            // Each measurement brings both halves, so the systolic count is the number of measurements
            readingsCount = summary.readingsCount,
            outOfRangeCount = summary.outOfRangeCount + diastolic.outOfRangeCount,
            stability = maxOf(summary.stability, diastolic.stability)
        )
    }
}

private fun VitalSignSummary.toRow() = SummaryRow(
    type = type,
    // One decimal, as the history average: 76.3 lpm says more than 76 over a period
    averageText = "%.1f".format(averageValue),
    minText = type.format(minValue),
    maxText = type.format(maxValue),
    readingsCount = readingsCount,
    outOfRangeCount = outOfRangeCount,
    stability = stability
)
