package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.guardian_plus_mobile_app.core.session.LocalCareContext
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location.LocationHistoryScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location.LocationTrackingScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone.CreateSafeZoneScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone.SafeZonesScreen
import com.example.guardian_plus_mobile_app.navigation.LocationRoute
import kotlinx.serialization.Serializable

@Serializable
data object SafeZonesRoute

@Serializable
data object CreateSafeZoneRoute

@Serializable
data object LocationHistoryRoute

fun NavGraphBuilder.mobilityGeofencingNavGraph(
    navController: NavController
) {
    composable<LocationRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId.ifEmpty { "123e4567-e89b-12d3-a456-426614174000" }
        LocationTrackingScreen(
            fragileCitizenId = citizenId,
            onNavigateToHistory = {
                navController.navigate(LocationHistoryRoute)
            },
            onNavigateToSafeZones = {
                navController.navigate(SafeZonesRoute)
            }
        )
    }

    composable<SafeZonesRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId.ifEmpty { "123e4567-e89b-12d3-a456-426614174000" }
        SafeZonesScreen(
            fragileCitizenId = citizenId,
            onBackClick = {
                navController.popBackStack()
            },
            onNavigateToCreate = {
                navController.navigate(CreateSafeZoneRoute)
            }
        )
    }

    composable<CreateSafeZoneRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId.ifEmpty { "123e4567-e89b-12d3-a456-426614174000" }
        CreateSafeZoneScreen(
            fragileCitizenId = citizenId,
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable<LocationHistoryRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId.ifEmpty { "123e4567-e89b-12d3-a456-426614174000" }
        LocationHistoryScreen(
            fragileCitizenId = citizenId,
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}