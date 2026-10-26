package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.guardian_plus_mobile_app.core.session.LocalCareContext
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location.LocationTrackingScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone.SafeZonesScreen
import com.example.guardian_plus_mobile_app.navigation.LocationRoute
import kotlinx.serialization.Serializable


@Serializable
data object SafeZonesRoute

fun NavGraphBuilder.mobilityGeofencingNavGraph(
    navController: NavController
) {
    composable<LocationRoute> {
        val careContext = LocalCareContext.current
        LocationTrackingScreen(
            fragileCitizenId = careContext.careRecipientProfileId.ifEmpty { "123e4567-e89b-12d3-a456-426614174000" },
            onNavigateToSafeZones = {
                navController.navigate(SafeZonesRoute)
            }
        )
    }

    composable<SafeZonesRoute> {
        val careContext = LocalCareContext.current
        SafeZonesScreen(
            fragileCitizenId = careContext.careRecipientProfileId.ifEmpty { "123e4567-e89b-12d3-a456-426614174000" },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}