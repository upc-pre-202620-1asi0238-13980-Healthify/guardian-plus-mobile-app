package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.guardian_plus_mobile_app.core.session.LocalCareContext
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication.DeviceStatusCheckScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication.DirectCommunicationEntryScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication.UnansweredCallScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication.VideoCallScreen
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication.VoiceCallScreen
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

// US-23: Direct Communication Channel Routes
@Serializable
data object DirectCommunicationRoute

@Serializable
data object VideoCallRoute

@Serializable
data object VoiceCallRoute

@Serializable
data object DeviceStatusCheckRoute

@Serializable
data object UnansweredCallRoute

fun NavGraphBuilder.mobilityGeofencingNavGraph(
    navController: NavController
) {
    composable<LocationRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId

        LocationTrackingScreen(
            fragileCitizenId = citizenId,
            onNavigateToHistory = {
                navController.navigate(LocationHistoryRoute)
            },
            onNavigateToSafeZones = {
                navController.navigate(SafeZonesRoute)
            },
            onNavigateToVideoCall = {
                navController.navigate(DirectCommunicationRoute)
            },
            onNavigateToVoiceCall = {
                navController.navigate(VoiceCallRoute)
            },
            onNavigateToDeviceStatusCheck = {
                navController.navigate(DeviceStatusCheckRoute)
            },
            onNavigateToUnansweredCall = {
                navController.navigate(UnansweredCallRoute)
            }
        )
    }

    composable<SafeZonesRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId

        SafeZonesScreen(
            fragileCitizenId = citizenId,
            onBackClick = { navController.popBackStack() },
            onNavigateToCreate = { navController.navigate(CreateSafeZoneRoute) },
            onNavigateToEdit = { id, name, lat, lng, radius ->
                navController.navigate(EditSafeZoneRoute(id, name, lat, lng, radius))
            }
        )
    }

    composable<CreateSafeZoneRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId

        CreateSafeZoneScreen(
            fragileCitizenId = citizenId,
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable<LocationHistoryRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId

        LocationHistoryScreen(
            fragileCitizenId = citizenId,
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable<EditSafeZoneRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<EditSafeZoneRoute>()
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId

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

    // US-23: Evaluation & Entry Route connected to LocalCareContext.current.careRecipientProfileId
    composable<DirectCommunicationRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId

        DirectCommunicationEntryScreen(
            fragileCitizenId = citizenId,
            onNavigateToVideoCall = {
                navController.navigate(VideoCallRoute) {
                    popUpTo<DirectCommunicationRoute> { inclusive = true }
                }
            },
            onNavigateToVoiceCall = {
                navController.navigate(VoiceCallRoute) {
                    popUpTo<DirectCommunicationRoute> { inclusive = true }
                }
            },
            onNavigateToDeviceStatusCheck = {
                navController.navigate(DeviceStatusCheckRoute) {
                    popUpTo<DirectCommunicationRoute> { inclusive = true }
                }
            },
            onBackClick = { navController.popBackStack() }
        )
    }

    // US-23: Direct Communication Screens
    composable<VideoCallRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId

        VideoCallScreen(
            fragileCitizenId = citizenId,
            onBackClick = { navController.popBackStack() },
            onFallbackToVoice = {
                navController.navigate(VoiceCallRoute) {
                    popUpTo<VideoCallRoute> { inclusive = true }
                }
            },
            onCallUnanswered = {
                navController.navigate(UnansweredCallRoute) {
                    popUpTo<VideoCallRoute> { inclusive = true }
                }
            }
        )
    }

    composable<VoiceCallRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId

        VoiceCallScreen(
            fragileCitizenId = citizenId,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable<DeviceStatusCheckRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId

        DeviceStatusCheckScreen(
            fragileCitizenId = citizenId,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable<UnansweredCallRoute> {
        val careContext = LocalCareContext.current
        val citizenId = careContext.careRecipientProfileId

        UnansweredCallScreen(
            fragileCitizenId = citizenId,
            onBackClick = { navController.popBackStack() },
            onRetryCall = {
                navController.navigate(DirectCommunicationRoute) {
                    popUpTo<UnansweredCallRoute> { inclusive = true }
                }
            },
            onNavigateToLastLocation = {
                navController.navigate(LocationRoute) {
                    popUpTo<UnansweredCallRoute> { inclusive = true }
                }
            }
        )
    }
}
