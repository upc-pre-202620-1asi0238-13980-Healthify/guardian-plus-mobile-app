package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.push

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.navigation.AppointmentsRoute
import com.example.guardian_plus_mobile_app.features.profile.presentation.navigation.SessionStartRoute

/**
 * Opens the appointments of the person under care when the push reminder of one of them was tapped. It waits
 * until an account is signed in and its session has finished loading, so the list lands on top of the app.
 */
@Composable
fun OpenAppointmentsFromNotification(
    navController: NavHostController,
    requested: Boolean,
    signedIn: Boolean,
    onOpened: () -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val loadingSession = backStackEntry?.destination?.hasRoute(SessionStartRoute::class) == true
    LaunchedEffect(requested, signedIn, loadingSession, backStackEntry == null) {
        if (!requested || !signedIn || loadingSession || backStackEntry == null) return@LaunchedEffect
        navController.navigate(AppointmentsRoute) { launchSingleTop = true }
        onOpened()
    }
}
