package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.push

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderNotificationsTest {

    @Test
    fun aPushMarkedAsAppointmentReminderIsAReminder() {
        val data = mapOf("kind" to "APPOINTMENT_REMINDER", "reminderId" to "r-1", "careRecipientProfileId" to "p-1")

        assertTrue(ReminderNotifications.isAppointmentReminder(data))
    }

    @Test
    fun anAlertPushIsNotAReminder() {
        val data = mapOf("alertId" to "a-1", "purpose" to "ALERT", "severity" to "CRITICAL", "audible" to "true")

        assertFalse(ReminderNotifications.isAppointmentReminder(data))
    }
}
