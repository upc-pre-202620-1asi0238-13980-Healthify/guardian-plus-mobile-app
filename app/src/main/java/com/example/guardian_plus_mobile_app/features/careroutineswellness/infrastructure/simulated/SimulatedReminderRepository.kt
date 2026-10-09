package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.simulated

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * Sample day of the routines prototype, served locally until the app reads /reminders from the platform.
 * The schedule is fixed; what already passed counts as confirmed and the rest is still scheduled.
 */
class SimulatedReminderRepository @Inject constructor() : ReminderRepository {

    override suspend fun getReminders(
        personUnderCareId: String,
        from: Instant?,
        to: Instant?,
        type: ReminderType?
    ): Result<List<Reminder>> {
        val reminders = sampleReminders(personUnderCareId, Instant.now())
            .filter { from == null || !it.scheduledTime.isBefore(from) }
            .filter { to == null || it.scheduledTime.isBefore(to) }
            .filter { type == null || it.type == type }
            .sortedBy { it.scheduledTime }
        return Result.success(reminders)
    }

    private fun sampleReminders(personUnderCareId: String, now: Instant): List<Reminder> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        fun today(hour: Int, minute: Int): Instant = today.atTime(LocalTime.of(hour, minute)).atZone(zone).toInstant()
        fun statusAt(time: Instant) = if (time.isBefore(now)) ReminderStatus.CONFIRMED else ReminderStatus.SCHEDULED

        fun reminder(
            id: String,
            type: ReminderType,
            title: String,
            scheduledTime: Instant,
            dosage: String? = null,
            instructions: String? = null,
            location: String? = null,
            durationMinutes: Int? = null
        ) = Reminder(
            id = id,
            personUnderCareId = personUnderCareId,
            type = type,
            title = title,
            dosage = dosage,
            instructions = instructions,
            location = location,
            durationMinutes = durationMinutes,
            scheduledTime = scheduledTime,
            status = statusAt(scheduledTime)
        )

        return listOf(
            reminder(
                id = "rem-omeprazol",
                type = ReminderType.MEDICATION,
                title = "Omeprazol 20 mg",
                dosage = "1 cápsula",
                instructions = "En ayunas",
                scheduledTime = today(7, 0)
            ),
            reminder(
                id = "rem-metformina",
                type = ReminderType.MEDICATION,
                title = "Metformina 850 mg",
                dosage = "1 tableta",
                instructions = "Con el desayuno",
                scheduledTime = today(8, 0)
            ),
            reminder(
                id = "rem-movilidad",
                type = ReminderType.PHYSICAL_ACTIVITY,
                title = "Movilidad de hombros",
                durationMinutes = 5,
                scheduledTime = today(9, 30)
            ),
            reminder(
                id = "rem-analisis",
                type = ReminderType.APPOINTMENT,
                title = "Análisis de sangre",
                location = "Laboratorio Clínico Central",
                scheduledTime = today(11, 0)
            ),
            reminder(
                id = "rem-caminata",
                type = ReminderType.PHYSICAL_ACTIVITY,
                title = "Caminata suave",
                durationMinutes = 10,
                scheduledTime = today(12, 30)
            ),
            reminder(
                id = "rem-losartan",
                type = ReminderType.MEDICATION,
                title = "Losartán 50 mg",
                dosage = "1 tableta",
                instructions = "Después del almuerzo",
                scheduledTime = today(14, 0)
            ),
            reminder(
                id = "rem-estiramiento",
                type = ReminderType.PHYSICAL_ACTIVITY,
                title = "Estiramiento de piernas",
                durationMinutes = 5,
                scheduledTime = today(17, 0)
            ),
            reminder(
                id = "rem-cardiologia",
                type = ReminderType.APPOINTMENT,
                title = "Control de cardiología",
                location = "Clínica San Gabriel · Consultorio 204",
                scheduledTime = today.plusDays(1).atTime(LocalTime.of(10, 30)).atZone(zone).toInstant()
            )
        )
    }
}
