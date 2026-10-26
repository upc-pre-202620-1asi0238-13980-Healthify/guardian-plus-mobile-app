package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.push

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.navigation.AlertDetailRoute
import com.example.guardian_plus_mobile_app.features.profile.presentation.navigation.SessionStartRoute

/**
 * Opens the detail of the alert whose push notification was tapped (first step of the emergency route). It waits
 * until an account is signed in and its session has finished loading, so the detail lands on top of the app.
 */
@Composable
fun OpenAlertFromNotification(
    navController: NavHostController,
    alertId: String?,
    signedIn: Boolean,
    onOpened: () -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val loadingSession = backStackEntry?.destination?.hasRoute(SessionStartRoute::class) == true
    LaunchedEffect(alertId, signedIn, loadingSession, backStackEntry == null) {
        if (alertId == null || !signedIn || loadingSession || backStackEntry == null) return@LaunchedEffect
        navController.navigate(AlertDetailRoute(alertId = alertId)) { launchSingleTop = true }
        onOpened()
    }
}
