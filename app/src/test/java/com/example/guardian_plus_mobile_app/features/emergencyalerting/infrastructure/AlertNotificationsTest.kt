package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure

import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.push.AlertNotifications
import org.junit.Assert.assertEquals
import org.junit.Test

class AlertNotificationsTest {

    @Test
    fun `audible alerts use the channel with sound`() {
        assertEquals("guardian_alerts", AlertNotifications.channelFor(audible = true))
    }

    @Test
    fun `alerts silenced by silent mode use the quiet channel`() {
        assertEquals("guardian_alerts_silent", AlertNotifications.channelFor(audible = false))
    }
}
