package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.CurrentLocation
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationTrackingUiStateTest {

    private fun location(status: String) = CurrentLocation(
        fragileCitizenId = "profile-1",
        latitude = -12.05,
        longitude = -77.04,
        accuracyInMeters = 8.0,
        status = status,
        recordedAt = "2026-10-28T15:00:00Z"
    )

    @Test
    fun `a location outside the active zone is shown as outside`() {
        assertFalse(LocationTrackingUiState.Success(location("OUTSIDE_SAFE_ZONE"), null).isWithinSafeZone)
    }

    @Test
    fun `a location inside the active zone is shown as inside`() {
        assertTrue(LocationTrackingUiState.Success(location("WITHIN_SAFE_ZONE"), null).isWithinSafeZone)
    }
}
