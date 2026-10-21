package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.RecurrenceFrequency
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

/**
 * First occurrence of a routine set for [time]: today if that time has not passed yet, otherwise the next
 * day; for "Días específicos", the next of the chosen [days]. The platform repeats it from there.
 */
fun firstOccurrence(
    time: LocalTime,
    frequency: RecurrenceFrequency,
    days: Set<DayOfWeek>,
    now: Instant,
    zone: ZoneId = ZoneId.systemDefault()
): Instant {
    var candidate = now.atZone(zone).toLocalDate().atTime(time).atZone(zone)
    if (!candidate.toInstant().isAfter(now)) candidate = candidate.plusDays(1)
    if (frequency == RecurrenceFrequency.WEEKLY && days.isNotEmpty()) {
        while (candidate.dayOfWeek !in days) candidate = candidate.plusDays(1)
    }
    return candidate.toInstant()
}
