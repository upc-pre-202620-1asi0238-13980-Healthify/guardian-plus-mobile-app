package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.guardian_plus_mobile_app.core.session.LocalCareContext
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location.LocationHistoryScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location.LocationTrackingScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone.CreateSafeZoneScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone.EditSafeZoneScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone.SafeZonesScreen
import com.example.guardian_plus_mobile_app.navigation.LocationRoute
import kotlinx.serialization.Serializable

@Serializable
data object SafeZonesRoute

@Serializable
data object CreateSafeZoneRoute

@Serializable
data object LocationHistoryRoute
@Serializable
data class EditSafeZoneRoute(
    val safeZoneId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Double
)
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
    composable<SafeZonesRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId.ifEmpty { "123e4567-e89b-12d3-a456-426614174000" }
        SafeZonesScreen(
            fragileCitizenId = citizenId,
            onBackClick = { navController.popBackStack() },
            onNavigateToCreate = { navController.navigate(CreateSafeZoneRoute) },
            onNavigateToEdit = { id, name, lat, lng, radius ->
                navController.navigate(EditSafeZoneRoute(id, name, lat, lng, radius))
            }
        )
    }

    composable<EditSafeZoneRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<EditSafeZoneRoute>()
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId.ifEmpty { "123e4567-e89b-12d3-a456-426614174000" }

        EditSafeZoneScreen(
            safeZoneId = route.safeZoneId,
            initialName = route.name,
            initialLat = route.latitude,
            initialLng = route.longitude,
            initialRadius = route.radius,
            fragileCitizenId = citizenId,
            onBackClick = { navController.popBackStack() }
        )
    }
}