package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitor
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlan
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecord
import java.time.Instant

/** Completed, pending and missed reminders of one group, as counted on the routines screen. */
data class RoutineCount(val completed: Int, val pending: Int, val missed: Int) {
    val total: Int get() = completed + pending + missed

    val percentage: Int get() = if (total == 0) 0 else completed * 100 / total
}

data class RoutinesUiState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null,
    val careRecipientName: String = "",
    val todayReminders: List<Reminder> = emptyList(),
    val nextAppointment: Reminder? = null,
    // The optional sections stay null when the platform has nothing yet (no plan, no telemetry)
    val hydrationPlan: HydrationPlan? = null,
    val lastSleep: SleepCycleRecord? = null,
    val activityMonitor: ActivityMonitor? = null,
    val medicationStocks: List<MedicationStock> = emptyList(),
    // Server time of the last refresh; "EN 25 MIN" and "Hace 1 min" are measured against it
    val now: Instant = Instant.EPOCH
) {
    // Hydration follows its own plan, and its later occurrences of the day do not exist yet on the
    // platform (one occurrence at a time), so counting them would understate the day
    private val dayReminders: List<Reminder>
        get() = todayReminders.filter { it.type != ReminderType.HYDRATION && it.status.countsTowardsDay }

    val today: RoutineCount get() = dayReminders.toRoutineCount()

    val nextReminder: Reminder?
        get() = dayReminders.filter { it.status.isPending }.minByOrNull { it.scheduledTime }

    fun countOf(type: ReminderType): RoutineCount = dayReminders.filter { it.type == type }.toRoutineCount()

    /** The medication that runs out first. */
    val lowestStock: MedicationStock?
        get() = medicationStocks.minByOrNull { it.remainingDaysOfSupply }

    private fun List<Reminder>.toRoutineCount() = RoutineCount(
        completed = count { it.status == ReminderStatus.CONFIRMED },
        pending = count { it.status.isPending },
        missed = count { it.status == ReminderStatus.MISSED }
    )
}
