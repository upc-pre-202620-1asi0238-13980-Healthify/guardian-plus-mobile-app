package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.location.PlaceNameResolver
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.CurrentLocationDto
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.LocationTrackingService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories.AlertContextRepositoryImpl
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetLiveVitalSignsUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSign
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.ReadingClassification
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.VitalSignRepository
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
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

    /** Answers with [location], or 404 like Mobility does before the wristband sends a first position. */
    private class FakeLocationTrackingService(private val location: CurrentLocationDto?) : LocationTrackingService {
        val requestedProfileIds = mutableListOf<String>()

        override suspend fun getCurrentLocation(careRecipientProfileId: String): Response<CurrentLocationDto> {
            requestedProfileIds += careRecipientProfileId
            return location?.let { Response.success(it) } ?: Response.error(404, "".toResponseBody())
        }
    }

    private val now = Instant.parse("2026-10-22T15:00:00Z")
    private val home = CurrentLocationDto(
        fragileCitizenId = "profile-1",
        latitude = -12.046374,
        longitude = -77.042793,
        accuracyInMeters = 8.4,
        status = "WITHIN_SAFE_ZONE",
        recordedAt = "2026-10-22T14:59:30Z"
    )

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

    private fun readings(vararg readings: LiveVitalSign) =
        FakeVitalSignRepository(Result.success(LiveVitalSigns("profile-1", now, readings.toList())))

    private fun repository(
        vitalSigns: FakeVitalSignRepository = readings(),
        locations: FakeLocationTrackingService = FakeLocationTrackingService(null),
        placeNames: PlaceNameResolver = PlaceNameResolver { _, _ -> null }
    ) = AlertContextRepositoryImpl(GetLiveVitalSignsUseCase(vitalSigns), locations, placeNames)

    @Test
    fun `a fall shows the live heart rate, oxygen, wristband signal and where it happened`() = runBlocking {
        val vitalSigns = readings(reading(VitalSignType.HR, 112.4), reading(VitalSignType.SPO2, 95.0))
        val locations = FakeLocationTrackingService(home)

        val context = repository(vitalSigns, locations, { _, _ -> "Av. Larco 345 · Miraflores" })
            .getAlertContext(alert(AlertSourceType.FALL_DETECTED)).getOrThrow()

        assertEquals(listOf("profile-1"), vitalSigns.requestedProfileIds)
        assertEquals(listOf("profile-1"), locations.requestedProfileIds)
        assertEquals(112, context.heartRateBpm)
        assertEquals(95, context.oxygenSaturation)
        assertEquals("Pulsera conectada", context.deviceSummary)
        assertNull(context.readingSummary)
        assertEquals("Av. Larco 345 · Miraflores", context.locationName)
        assertEquals(Instant.parse("2026-10-22T14:59:30Z"), context.locationUpdatedAt)
    }

    @Test
    fun `a vital sign anomaly summarizes the readings out of range`() = runBlocking {
        val vitalSigns = readings(
            reading(VitalSignType.HR, 124.0, ReadingClassification.ABOVE_RANGE),
            reading(VitalSignType.SPO2, 89.0, ReadingClassification.BELOW_RANGE),
            reading(VitalSignType.TEMP, 36.6)
        )

        val context = repository(vitalSigns).getAlertContext(alert(AlertSourceType.VITAL_SIGN_ANOMALY)).getOrThrow()

        assertEquals("124 lpm · 2 lecturas fuera de rango", context.readingSummary)
    }

    @Test
    fun `readings older than the live window are not shown as current`() = runBlocking {
        val vitalSigns = readings(reading(VitalSignType.HR, 80.0, liveSignal = false))

        val context = repository(vitalSigns).getAlertContext(alert(AlertSourceType.SOS_TRIGGERED)).getOrThrow()

        assertNull(context.heartRateBpm)
        assertEquals("Pulsera sin señal", context.deviceSummary)
    }

    @Test
    fun `a safe zone violation shows only the location, with coordinates when the place has no name`() = runBlocking {
        val vitalSigns = readings(reading(VitalSignType.HR, 80.0))

        val context = repository(vitalSigns, FakeLocationTrackingService(home))
            .getAlertContext(alert(AlertSourceType.SAFE_ZONE_VIOLATION)).getOrThrow()

        assertTrue(vitalSigns.requestedProfileIds.isEmpty())
        assertNull(context.heartRateBpm)
        assertEquals("-12.04637, -77.04279", context.locationName)
    }

    @Test
    fun `alerts about routines ask neither for readings nor for the location`() = runBlocking {
        val vitalSigns = readings(reading(VitalSignType.HR, 80.0))
        val locations = FakeLocationTrackingService(home)

        val context = repository(vitalSigns, locations)
            .getAlertContext(alert(AlertSourceType.MEDICATION_RESTOCK_SUGGESTED)).getOrThrow()

        assertEquals(AlertContext(), context)
        assertTrue(vitalSigns.requestedProfileIds.isEmpty())
        assertTrue(locations.requestedProfileIds.isEmpty())
    }

    @Test
    fun `a part that cannot be read is left out without hiding the other`() = runBlocking {
        val noWristband = FakeVitalSignRepository(Result.failure(IllegalStateException("sin pulsera")))

        val withoutReadings = repository(noWristband, FakeLocationTrackingService(home), { _, _ -> "Casa" })
            .getAlertContext(alert(AlertSourceType.FALL_DETECTED)).getOrThrow()
        val withoutLocation = repository(readings(reading(VitalSignType.HR, 90.0)))
            .getAlertContext(alert(AlertSourceType.FALL_DETECTED)).getOrThrow()

        assertNull(withoutReadings.heartRateBpm)
        assertEquals("Casa", withoutReadings.locationName)
        assertEquals(90, withoutLocation.heartRateBpm)
        assertNull(withoutLocation.locationName)
    }
}
