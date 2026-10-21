package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Start of the day of [now] and of the next one, on the phone's time zone: the [from, to) of "today". */
fun todayBounds(now: Instant, zone: ZoneId = ZoneId.systemDefault()): Pair<Instant, Instant> {
    val today = now.atZone(zone).toLocalDate()
    return today.atStartOfDay(zone).toInstant() to today.plusDays(1).atStartOfDay(zone).toInstant()
}

/** The last seven days, today included, as the platform's adherence endpoint counts them. */
fun lastWeek(now: Instant, zone: ZoneId = ZoneId.systemDefault()): Pair<LocalDate, LocalDate> {
    val today = now.atZone(zone).toLocalDate()
    return today.minusDays(6) to today
}
