package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatDuration
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val spanish: Locale = Locale.forLanguageTag("es")
private val weekdayAndDay: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE d", spanish)
private val dayAndMonth: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM", spanish)

@DrawableRes
fun ReminderType.iconRes(): Int = when (this) {
    ReminderType.MEDICATION -> R.drawable.ic_pill
    ReminderType.APPOINTMENT -> R.drawable.ic_calendar
    ReminderType.PHYSICAL_ACTIVITY -> R.drawable.ic_activity
    ReminderType.HYDRATION -> R.drawable.ic_droplet
}

/** The reminder's own title, or the routine it belongs to when the family did not name it. */
@Composable
fun Reminder.displayTitle(): String = title?.takeIf { it.isNotBlank() } ?: stringResource(
    when (type) {
        ReminderType.MEDICATION -> R.string.routines_category_medication
        ReminderType.APPOINTMENT -> R.string.routines_category_appointments
        ReminderType.PHYSICAL_ACTIVITY -> R.string.routines_category_activity
        ReminderType.HYDRATION -> R.string.routines_category_hydration
    }
)

/** "1 tableta · Después del almuerzo", "Clínica San Gabriel" or "10 minutos", depending on the routine. */
@Composable
fun Reminder.detailLine(): String {
    val duration = durationMinutes?.let { stringResource(R.string.routines_activity_duration, it) }
    return listOfNotNull(dosage, instructions, location, duration)
        .filter { it.isNotBlank() }
        .joinToString(" · ")
}

/** "EN 25 MIN", "EN 2 H", "EN 1 H 20 MIN", or "AHORA" once the time has come. */
@Composable
fun timeUntil(at: Instant, now: Instant): String {
    val minutes = Duration.between(now, at).toMinutes()
    return when {
        minutes <= 0 -> stringResource(R.string.routines_next_now)
        minutes < 60 -> stringResource(R.string.routines_next_in_minutes, minutes.toInt())
        minutes % 60 == 0L -> stringResource(R.string.routines_next_in_hours, (minutes / 60).toInt())
        else -> stringResource(R.string.routines_next_in_hours_minutes, (minutes / 60).toInt(), (minutes % 60).toInt())
    }
}

/** "hoy", "mañana" or "24 abr" on the phone's time zone. */
@Composable
fun dayLabel(at: Instant, now: Instant): String {
    val zone = ZoneId.systemDefault()
    val day = at.atZone(zone).toLocalDate()
    val today = now.atZone(zone).toLocalDate()
    return when (day) {
        today -> stringResource(R.string.routines_day_today)
        today.plusDays(1) -> stringResource(R.string.routines_day_tomorrow)
        else -> dayAndMonth.format(day)
    }
}

/** "24 abr" on the phone's time zone. */
fun formatDayAndMonth(at: Instant): String = dayAndMonth.format(at.atZone(ZoneId.systemDefault()))

/** "MARTES 23", the date of the "HOY" card. */
fun formatWeekdayAndDay(at: Instant): String =
    weekdayAndDay.format(at.atZone(ZoneId.systemDefault())).uppercase(spanish)

/** 444 → "7 h 24 min". */
@Composable
fun formatSleepDuration(minutes: Long): String =
    stringResource(R.string.duration_hours, (minutes / 60).toInt(), (minutes % 60).toInt())

/** "25 min" or "1 h 05 min" until [at]; never negative. */
@Composable
fun durationUntil(at: Instant, now: Instant): String =
    formatDuration(Duration.between(now, at).seconds.coerceAtLeast(60))

/** "Aviso 1 hora antes", "Aviso 1 día antes", "Aviso 30 min antes" or "Sin aviso previo". */
@Composable
fun leadTimeLabel(minutes: Int): String = when {
    minutes <= 0 -> stringResource(R.string.routines_lead_none)
    minutes % MINUTES_PER_DAY == 0 -> pluralStringResource(R.plurals.routines_lead_days, minutes / MINUTES_PER_DAY, minutes / MINUTES_PER_DAY)
    minutes % 60 == 0 -> pluralStringResource(R.plurals.routines_lead_hours, minutes / 60, minutes / 60)
    else -> stringResource(R.string.routines_lead_minutes, minutes)
}

private const val MINUTES_PER_DAY = 1_440

/** "12 de mayo". */
fun formatDayOfMonth(date: LocalDate): String = dayOfMonth.format(date)

/** "Lun", "Mar"… for the rows of a weekly history. */
fun formatShortWeekday(date: LocalDate): String =
    shortWeekday.format(date).replace(".", "").replaceFirstChar { it.titlecase(spanish) }

/** "L", "M", "X"… the single-letter days under the weekly bars. */
fun weekdayInitial(day: DayOfWeek): String = when (day) {
    DayOfWeek.MONDAY -> "L"
    DayOfWeek.TUESDAY -> "M"
    DayOfWeek.WEDNESDAY -> "X"
    DayOfWeek.THURSDAY -> "J"
    DayOfWeek.FRIDAY -> "V"
    DayOfWeek.SATURDAY -> "S"
    DayOfWeek.SUNDAY -> "D"
}

/** "22:00" for a time of day. */
fun formatTimeOfDay(time: LocalTime): String = timeOfDay.format(time)

private val dayOfMonth: DateTimeFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM", spanish)
private val shortWeekday: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE", spanish)
private val timeOfDay: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
