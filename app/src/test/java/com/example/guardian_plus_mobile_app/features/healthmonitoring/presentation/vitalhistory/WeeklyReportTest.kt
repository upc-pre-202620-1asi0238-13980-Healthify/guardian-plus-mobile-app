package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.StabilityIndex
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.previewHealthReport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WeeklyReportTest {

    // Preview: six signs of 2016 readings each, two systolic readings out of range
    private val report = previewHealthReport()

    @Test
    fun `stability is the share of readings within range`() {
        // 12096 readings, 2 out of range
        assertEquals(100, WeeklyReport(report, null, null).stabilityPercentage)

        val shaky = report.copy(summaries = report.summaries.map { it.copy(outOfRangeCount = 403) })
        assertEquals(80, WeeklyReport(shaky, null, null).stabilityPercentage)
    }

    @Test
    fun `no readings means no stability figure`() {
        assertNull(WeeklyReport(report.copy(summaries = emptyList()), null, null).stabilityPercentage)
    }

    @Test
    fun `parameters go from most to least worrying`() {
        val mixed = report.copy(
            summaries = report.summaries.map {
                when (it.type) {
                    VitalSignType.SPO2 -> it.copy(outOfRangeCount = 4, stability = StabilityIndex.RECURRENT)
                    VitalSignType.TEMP -> it.copy(outOfRangeCount = 1, stability = StabilityIndex.UNSTABLE)
                    else -> it
                }
            }
        )
        val weekly = WeeklyReport(mixed, null, null)
        assertEquals(
            listOf(VitalSignType.SPO2, VitalSignType.BP_SYS, VitalSignType.TEMP),
            weekly.parameters.take(3).map { it.type }
        )
        assertEquals(listOf(VitalSignType.SPO2), weekly.recurrentParameters.map { it.type })
        assertTrue(weekly.parameters.drop(3).all { it.stability == StabilityIndex.STABLE })
    }
}
