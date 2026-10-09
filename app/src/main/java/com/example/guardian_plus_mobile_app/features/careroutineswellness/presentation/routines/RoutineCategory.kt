package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepClassification
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTone
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.dayLabel
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.formatDayAndMonth
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.formatSleepDuration
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import java.time.ZoneId

/** The seven routines of the "Todas las rutinas" list, in the order of the prototype. */
enum class RoutineCategory(
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val titleRes: Int,
    val tone: RoutineTone
) {
    MEDICATION(R.drawable.ic_pill, R.string.routines_category_medication, RoutineTone.MINT),
    APPOINTMENTS(R.drawable.ic_calendar, R.string.routines_category_appointments, RoutineTone.BLUE),
    ACTIVITY(R.drawable.ic_activity, R.string.routines_category_activity, RoutineTone.MINT),
    HYDRATION(R.drawable.ic_droplet, R.string.routines_category_hydration, RoutineTone.BLUE),
    SLEEP(R.drawable.ic_moon, R.string.routines_category_sleep, RoutineTone.VIOLET),
    INACTIVITY(R.drawable.ic_triangle_alert, R.string.routines_category_inactivity, RoutineTone.ORANGE),
    STOCK(R.drawable.ic_layers, R.string.routines_category_stock, RoutineTone.YELLOW)
}

/** Second line of a category card and its optional badge ("1 pendiente", "Fragmentado", "Reponer"…). */
data class RoutineCategorySummary(
    val detail: String,
    val badge: String? = null,
    val badgeTone: RoutineTone = RoutineTone.MINT
)

@Composable
fun RoutinesUiState.summaryOf(category: RoutineCategory): RoutineCategorySummary = when (category) {
    RoutineCategory.MEDICATION -> {
        val count = countOf(ReminderType.MEDICATION)
        if (count.total == 0) {
            RoutineCategorySummary(detail = stringResource(R.string.routines_medication_none))
        } else {
            RoutineCategorySummary(
                detail = pluralStringResource(R.plurals.routines_medication_doses_today, count.total, count.total),
                badge = if (count.pending > 0) {
                    pluralStringResource(R.plurals.routines_pending_count, count.pending, count.pending)
                } else {
                    null
                },
                badgeTone = RoutineTone.YELLOW
            )
        }
    }

    RoutineCategory.APPOINTMENTS -> RoutineCategorySummary(
        detail = nextAppointment?.let {
            stringResource(R.string.routines_appointment_next, dayLabel(it.scheduledTime, now), formatClockTime(it.scheduledTime))
        } ?: stringResource(R.string.routines_appointment_none)
    )

    RoutineCategory.ACTIVITY -> {
        val count = countOf(ReminderType.PHYSICAL_ACTIVITY)
        if (count.total == 0) {
            RoutineCategorySummary(detail = stringResource(R.string.routines_activity_none))
        } else {
            RoutineCategorySummary(
                detail = stringResource(R.string.routines_activity_progress, count.completed, count.total),
                badge = stringResource(R.string.routines_percent, count.percentage)
            )
        }
    }

    RoutineCategory.HYDRATION -> hydrationPlan?.let { plan ->
        RoutineCategorySummary(
            detail = if (plan.intervalHours == 1) {
                stringResource(R.string.routines_hydration_every_hour)
            } else {
                stringResource(R.string.routines_hydration_every_hours, plan.intervalHours)
            },
            badge = stringResource(if (plan.active) R.string.routines_hydration_active else R.string.routines_hydration_paused),
            badgeTone = if (plan.active) RoutineTone.MINT else RoutineTone.MUTED
        )
    } ?: RoutineCategorySummary(detail = stringResource(R.string.routines_hydration_none))

    RoutineCategory.SLEEP -> lastSleep?.let { sleep ->
        val zone = ZoneId.systemDefault()
        val endedToday = sleep.endTime.atZone(zone).toLocalDate() == now.atZone(zone).toLocalDate()
        val duration = formatSleepDuration(sleep.durationMinutes)
        val fragmented = sleep.classification == SleepClassification.FRAGMENTED
        RoutineCategorySummary(
            detail = if (endedToday) {
                stringResource(R.string.routines_sleep_last_night, duration)
            } else {
                stringResource(R.string.routines_sleep_on_date, duration, formatDayAndMonth(sleep.endTime))
            },
            badge = stringResource(if (fragmented) R.string.routines_sleep_fragmented else R.string.routines_sleep_regular),
            badgeTone = if (fragmented) RoutineTone.ORANGE else RoutineTone.MINT
        )
    } ?: RoutineCategorySummary(detail = stringResource(R.string.routines_sleep_none))

    RoutineCategory.INACTIVITY -> activityMonitor?.let { monitor ->
        val inactiveSince = monitor.inactivitySince
        when {
            // The only case worth a badge: the person has not moved for longer than the threshold
            monitor.status == ActivityStatus.INACTIVITY_DETECTED && inactiveSince != null -> RoutineCategorySummary(
                detail = stringResource(R.string.routines_inactivity_detected, formatClockTime(inactiveSince)),
                badge = stringResource(R.string.routines_inactivity_badge),
                badgeTone = RoutineTone.ORANGE
            )
            monitor.detectionEnabled -> RoutineCategorySummary(detail = stringResource(R.string.routines_inactivity_watching))
            else -> RoutineCategorySummary(detail = stringResource(R.string.routines_inactivity_disabled))
        }
    } ?: RoutineCategorySummary(detail = stringResource(R.string.routines_inactivity_none))

    RoutineCategory.STOCK -> lowestStock?.let { stock ->
        val days = stock.remainingDaysOfSupply.toInt()
        RoutineCategorySummary(
            detail = pluralStringResource(R.plurals.routines_stock_supply, days, stock.medicationName, days),
            badge = if (medicationStocks.any { it.restockRecommended }) stringResource(R.string.routines_stock_restock) else null,
            badgeTone = RoutineTone.ORANGE
        )
    } ?: RoutineCategorySummary(detail = stringResource(R.string.routines_stock_none))
}
