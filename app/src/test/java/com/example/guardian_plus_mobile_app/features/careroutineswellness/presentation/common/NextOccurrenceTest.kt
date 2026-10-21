package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.RecurrenceFrequency
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class NextOccurrenceTest {

    private val lima = ZoneId.of("America/Lima")

    // Wednesday 21 October 2026, 12:00 in Lima
    private val now = LocalDateTime.of(2026, 10, 21, 12, 0).atZone(lima).toInstant()

    private fun at(day: Int, hour: Int, minute: Int = 0) = LocalDateTime.of(2026, 10, day, hour, minute).atZone(lima).toInstant()

    @Test
    fun `a time still ahead today starts today`() {
        assertEquals(at(21, 14), firstOccurrence(LocalTime.of(14, 0), RecurrenceFrequency.DAILY, emptySet(), now, lima))
    }

    @Test
    fun `a time already past starts tomorrow`() {
        assertEquals(at(22, 8), firstOccurrence(LocalTime.of(8, 0), RecurrenceFrequency.ONCE, emptySet(), now, lima))
    }

    @Test
    fun `the current minute counts as past`() {
        assertEquals(at(22, 12), firstOccurrence(LocalTime.of(12, 0), RecurrenceFrequency.DAILY, emptySet(), now, lima))
    }

    @Test
    fun `specific days start on the next chosen day`() {
        val mondayAndFriday = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
        assertEquals(at(23, 9), firstOccurrence(LocalTime.of(9, 0), RecurrenceFrequency.WEEKLY, mondayAndFriday, now, lima))
    }

    @Test
    fun `today is a chosen day when its time is still ahead`() {
        val wednesday = setOf(DayOfWeek.WEDNESDAY)
        assertEquals(at(21, 20), firstOccurrence(LocalTime.of(20, 0), RecurrenceFrequency.WEEKLY, wednesday, now, lima))
        assertEquals(at(28, 9), firstOccurrence(LocalTime.of(9, 0), RecurrenceFrequency.WEEKLY, wednesday, now, lima))
    }
}
