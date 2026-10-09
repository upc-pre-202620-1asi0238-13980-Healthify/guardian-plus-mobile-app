package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Adherence
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.StabilityIndex
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.SummaryRow
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.summaryRows
import kotlin.math.roundToInt

/**
 * What "Reporte semanal" shows: the platform's report of the week, plus the alerts and the medication of the
 * same days, which belong to other contexts. Either of those may be missing if its request failed.
 */
data class WeeklyReport(
    val report: HealthReport,
    val alertsTriggered: Long?,
    val medicationAdherence: Adherence?
) {
    /** Share of the week's readings that stayed within their normal range, every sign together. */
    val stabilityPercentage: Int?
        get() = report.readingsCount.takeIf { it > 0 }?.let { total ->
            ((total - report.outOfRangeCount) * 100.0 / total).roundToInt()
        }

    // Most worrying first: recurrent, then unstable, then stable; more anomalies first within each
    val parameters: List<SummaryRow>
        get() = report.summaryRows().sortedWith(
            compareByDescending<SummaryRow> { it.stability }.thenByDescending { it.outOfRangeCount }
        )

    val recurrentParameters: List<SummaryRow> get() = parameters.filter { it.stability == StabilityIndex.RECURRENT }
}
