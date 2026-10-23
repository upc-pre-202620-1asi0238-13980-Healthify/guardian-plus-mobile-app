package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories.AlertContextRepositoryImpl
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetLiveVitalSignsUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSign
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.ReadingClassification
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.VitalSignRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class AlertContextRepositoryImplTest {

    /** Answers the live view with [liveVitalSigns] and records which care recipients it was asked about. */
    private class FakeVitalSignRepository(private val liveVitalSigns: Result<LiveVitalSigns>) : VitalSignRepository {
        val requestedProfileIds = mutableListOf<String>()

        override suspend fun getLiveVitalSigns(careRecipientProfileId: String): Result<LiveVitalSigns> {
            requestedProfileIds += careRecipientProfileId
            return liveVitalSigns
        }

        override suspend fun getVitalSignHistory(
            careRecipientProfileId: String,
            from: LocalDate,
            to: LocalDate
        ): Result<List<VitalSignReading>> = error("unused")
    }

    private val now = Instant.parse("2026-10-22T15:00:00Z")

    private fun alert(sourceType: AlertSourceType) = Alert(
        id = "alert-1",
        careRecipientProfileId = "profile-1",
        sourceType = sourceType,
        severity = Severity.CRITICAL,
        status = AlertStatus.TRIGGERED,
        currentRecipientLevel = null,
        triggeredAt = now
    )

    private fun reading(
        type: VitalSignType,
        value: Double,
        classification: ReadingClassification = ReadingClassification.WITHIN_RANGE,
        liveSignal: Boolean = true
    ) = LiveVitalSign(
        id = type.name,
        type = type,
        typeName = type.name,
        unit = "",
        value = value,
        measuredAt = now,
        normalMinimum = 0.0,
        normalMaximum = 0.0,
        classification = classification,
        liveSignal = liveSignal
    )

    private fun repositoryWith(vararg readings: LiveVitalSign) =
        FakeVitalSignRepository(Result.success(LiveVitalSigns("profile-1", now, readings.toList())))

    @Test
    fun `a fall shows the live heart rate, oxygen and wristband signal`() = runBlocking {
        val repository = repositoryWith(reading(VitalSignType.HR, 112.4), reading(VitalSignType.SPO2, 95.0))

        val context = AlertContextRepositoryImpl(GetLiveVitalSignsUseCase(repository))
            .getAlertContext(alert(AlertSourceType.FALL_DETECTED)).getOrThrow()

        assertEquals(listOf("profile-1"), repository.requestedProfileIds)
        assertEquals(112, context.heartRateBpm)
        assertEquals(95, context.oxygenSaturation)
        assertEquals("Pulsera conectada", context.deviceSummary)
        assertNull(context.readingSummary)
        assertNull(context.locationName)
    }

    @Test
    fun `a vital sign anomaly summarizes the readings out of range`() = runBlocking {
        val repository = repositoryWith(
            reading(VitalSignType.HR, 124.0, ReadingClassification.ABOVE_RANGE),
            reading(VitalSignType.SPO2, 89.0, ReadingClassification.BELOW_RANGE),
            reading(VitalSignType.TEMP, 36.6)
        )

        val context = AlertContextRepositoryImpl(GetLiveVitalSignsUseCase(repository))
            .getAlertContext(alert(AlertSourceType.VITAL_SIGN_ANOMALY)).getOrThrow()

        assertEquals("124 lpm · 2 lecturas fuera de rango", context.readingSummary)
    }

    @Test
    fun `readings older than the live window are not shown as current`() = runBlocking {
        val repository = repositoryWith(reading(VitalSignType.HR, 80.0, liveSignal = false))

        val context = AlertContextRepositoryImpl(GetLiveVitalSignsUseCase(repository))
            .getAlertContext(alert(AlertSourceType.SOS_TRIGGERED)).getOrThrow()

        assertNull(context.heartRateBpm)
        assertEquals("Pulsera sin señal", context.deviceSummary)
    }

    @Test
    fun `alerts not raised by the wristband do not ask for readings`() = runBlocking {
        val repository = repositoryWith(reading(VitalSignType.HR, 80.0))

        val context = AlertContextRepositoryImpl(GetLiveVitalSignsUseCase(repository))
            .getAlertContext(alert(AlertSourceType.MEDICATION_RESTOCK_SUGGESTED)).getOrThrow()

        assertEquals(AlertContext(), context)
        assertTrue(repository.requestedProfileIds.isEmpty())
    }

    @Test
    fun `a failed read of the readings is passed on`() = runBlocking {
        val repository = FakeVitalSignRepository(Result.failure(IllegalStateException("sin pulsera")))

        val result = AlertContextRepositoryImpl(GetLiveVitalSignsUseCase(repository))
            .getAlertContext(alert(AlertSourceType.FALL_DETECTED))

        assertTrue(result.isFailure)
    }
}
