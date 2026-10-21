package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.medication

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Adherence
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessage
import java.time.Instant

data class MedicationUiState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null,
    val todayReminders: List<Reminder> = emptyList(),
    // Null when the platform could not measure it; the rest of the screen still shows
    val weeklyAdherence: Adherence? = null,
    val now: Instant = Instant.EPOCH,
    // Dose being confirmed or marked as missed, so only its button shows progress
    val busyReminderId: String? = null,
    val message: RoutineMessage? = null
) {
    /** Today's doses that are the person's responsibility, in the order of the day. */
    val doses: List<Reminder>
        get() = todayReminders.filter { it.status.countsTowardsDay }.sortedBy { it.scheduledTime }

    val takenCount: Int get() = doses.count { it.status == ReminderStatus.CONFIRMED }

    /** The dose the person has to take next: the one already issued, or the next scheduled. */
    val nextDose: Reminder? get() = doses.firstOrNull { it.status.isPending }

    /** Doses reissued after 10 minutes without confirmation, waiting for the family to review them. */
    val unconfirmedDoses: List<Reminder> get() = doses.filter { it.status == ReminderStatus.REISSUED }
}
