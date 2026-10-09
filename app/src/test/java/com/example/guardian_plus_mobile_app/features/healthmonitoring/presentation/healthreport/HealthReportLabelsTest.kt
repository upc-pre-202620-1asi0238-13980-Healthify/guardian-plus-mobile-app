package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.StabilityIndex
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class HealthReportLabelsTest {

    @Test
    fun `blood pressure halves become one row`() {
        val rows = previewHealthReport().summaryRows()
        assertEquals(
            listOf(VitalSignType.HR, VitalSignType.BP_SYS, VitalSignType.SPO2, VitalSignType.TEMP, VitalSignType.RESP_RATE),
            rows.map { it.type }
        )
        val pressure = rows[1]
        assertEquals("118/76", pressure.averageText)
        assertEquals("146/80", pressure.maxText)
        assertEquals(2016, pressure.readingsCount)
    }

    @Test
    fun `blood pressure row takes the worse stability of its halves`() {
        // The preview systolic has two readings out of range, its diastolic none
        val pressure = previewHealthReport().summaryRows()[1]
        assertEquals(StabilityIndex.UNSTABLE, pressure.stability)
        assertEquals(2, pressure.outOfRangeCount)
    }

    @Test
    fun `period reads as a span within one year`() {
        assertEquals("3 – 9 oct 2026", previewHealthReport(periodEnd = LocalDate.parse("2026-10-09")).periodText())
    }

    @Test
    fun `period across months names both months`() {
        assertEquals("29 oct – 4 nov 2026", previewHealthReport(periodEnd = LocalDate.parse("2026-11-04")).periodText())
    }

    @Test
    fun `one day period reads as a single date`() {
        val day = previewHealthReport().copy(periodStart = LocalDate.parse("2026-10-09"))
        assertEquals("9 oct 2026", day.periodText())
    }
}
