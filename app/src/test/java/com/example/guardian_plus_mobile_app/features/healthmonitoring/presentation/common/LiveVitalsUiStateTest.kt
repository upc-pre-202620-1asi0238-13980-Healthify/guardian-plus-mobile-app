package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LiveVitalsUiStateTest {

    private val lima = ZoneId.of("America/Lima")

    private fun reading(type: VitalSignType, value: Double, at: Instant) =
        VitalSignReading(id = "$type@$at", type = type, value = value, measuredAt = at)

    // previewLiveVitals measures every sign one minute before previewNow
    private fun state(heartRate: Double, vararg earlier: Double) = LiveVitalsUiState(
        vitals = previewLiveVitals(heartRate = heartRate),
        now = previewNow,
        zone = lima,
        todayReadings = earlier.mapIndexed { index, value ->
            reading(VitalSignType.HR, value, previewNow.minusSeconds(600L - index * 60))
        }
    )

    @Test
    fun `small changes against the range read as stable`() {
        // HR range 60–100: the tolerance is 4 bpm
        assertEquals(VitalTrend.STABLE, state(78.0, 75.0, 76.0, 77.0).trend(VitalSignType.HR))
    }

    @Test
    fun `changes beyond a tenth of the range read as rising or falling`() {
        assertEquals(VitalTrend.RISING, state(90.0, 75.0, 76.0, 77.0).trend(VitalSignType.HR))
        assertEquals(VitalTrend.FALLING, state(65.0, 75.0, 76.0, 77.0).trend(VitalSignType.HR))
    }

    @Test
    fun `no trend without an earlier reading`() {
        assertNull(state(78.0).trend(VitalSignType.HR))
    }

    @Test
    fun `merged readings drop duplicates and other days`() {
        val at = previewNow.minusSeconds(60)
        val yesterday = previewNow.minusSeconds(86_400)
        val merged = state(78.0).withReadings(
            listOf(
                reading(VitalSignType.HR, 70.0, yesterday),
                reading(VitalSignType.HR, 78.0, at),
                // Same reading coming back from the history with another id
                VitalSignReading(id = "history-id", type = VitalSignType.HR, value = 78.0, measuredAt = at)
            )
        )
        assertEquals(listOf(78.0), merged.todayValues(VitalSignType.HR))
    }
}
