package com.example.guardian_plus_mobile_app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.session.CareContext
import com.example.guardian_plus_mobile_app.core.session.LocalCareContext
import com.example.guardian_plus_mobile_app.features.auth.presentation.LogoutViewModel
import com.example.guardian_plus_mobile_app.features.auth.presentation.navigation.ForgotPasswordRoute
import com.example.guardian_plus_mobile_app.features.auth.presentation.navigation.LoginRoute
import com.example.guardian_plus_mobile_app.features.auth.presentation.navigation.OtpVerificationRoute
import com.example.guardian_plus_mobile_app.features.auth.presentation.navigation.RegisterRoute
import com.example.guardian_plus_mobile_app.features.auth.presentation.navigation.ResetPasswordRoute
import com.example.guardian_plus_mobile_app.features.auth.presentation.navigation.authNavGraph
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.navigation.careRoutinesWellnessNavGraph
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.navigation.emergencyAlertingNavGraph
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.navigation.HomeRoute
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.navigation.healthMonitoringNavGraph
import com.example.guardian_plus_mobile_app.features.profile.presentation.navigation.OnboardingRoute
import com.example.guardian_plus_mobile_app.features.profile.presentation.navigation.SessionStartRoute
import com.example.guardian_plus_mobile_app.features.profile.presentation.navigation.profileNavGraph
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.navigation.mobilityGeofencingNavGraph

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Any = LoginRoute,
    careContext: CareContext? = null,
    logoutViewModel: LogoutViewModel = hiltViewModel()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    // The bar is only shown on the five main screens; details and full-screen alerts hide it
    val currentDestination = TopLevelDestination.entries.firstOrNull { destination ->
        backStackEntry?.destination?.hasRoute(destination.route::class) == true
    }
    // The login and the rest of IAM's screens draw behind the system bars and pad themselves
    val isOnLogin = listOf(
        LoginRoute::class,
        SessionStartRoute::class,
        OnboardingRoute::class,
        RegisterRoute::class,
        OtpVerificationRoute::class,
        ForgotPasswordRoute::class,
        ResetPasswordRoute::class
    ).any { route -> backStackEntry?.destination?.hasRoute(route) == true }
    val openTab: (TopLevelDestination) -> Unit = { destination ->
        navController.navigate(destination.route) {
            // Each tab keeps its own state (report, navigation rules). Anchored on Home rather than the graph's
            // start destination, which is not on the back stack once it has been left for good
            popUpTo<HomeRoute> { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    // The tabs keep their state (and ViewModels) while switching between them. That state belongs to one
    // account: it is dropped whenever a session ends or starts, or the next account would see the previous one's data
    val forgetTabStates: () -> Unit = {
        TopLevelDestination.entries.forEach { destination -> navController.clearBackStack(destination.route) }
    }
    val signOut: () -> Unit = {
        navController.navigate(LoginRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
        forgetTabStates()
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = if (isOnLogin) WindowInsets(0) else ScaffoldDefaults.contentWindowInsets,
        bottomBar = {
            if (currentDestination != null) {
                GuardianBottomBar(
                    selected = currentDestination,
                    onDestinationClick = openTab
                )
            }
        }
    ) { paddingValues ->
        // The screens behind the sign-in read whose data they show from here
        CompositionLocalProvider(LocalCareContext provides (careContext ?: SIGNED_OUT)) {
            NavHost(
                navController = navController,
                // Login, the care context loading or "Inicio", as MainActivity decides from what the device keeps
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues)
            ) {
                authNavGraph(
                    navController = navController,
                    onLoginSuccess = {
                        // Whom the account cares for is loaded before entering the app
                        navController.navigate(SessionStartRoute) {
                            popUpTo<LoginRoute> { inclusive = true }
                        }
                    }
                )
                profileNavGraph(
                    onReady = {
                        forgetTabStates()
                        // Back from Home leaves the app instead of returning to the loading or the onboarding
                        navController.navigate(HomeRoute) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onOnboardingRequired = { route ->
                        navController.navigate(route) {
                            popUpTo<SessionStartRoute> { inclusive = true }
                        }
                    },
                    onSignedOut = signOut
                )
                healthMonitoringNavGraph(
                    onOpenHealth = { openTab(TopLevelDestination.HEALTH) },
                    onOpenAlerts = { openTab(TopLevelDestination.ALERTS) },
                    onOpenLocation = { openTab(TopLevelDestination.LOCATION) },
                    onLogout = {
                        logoutViewModel.logout()
                        signOut()
                    }
                )
                careRoutinesWellnessNavGraph(navController = navController)
                mobilityGeofencingNavGraph(navController = navController)
                emergencyAlertingNavGraph(
                    navController = navController,
                    onOpenLocation = { openTab(TopLevelDestination.LOCATION) },
                    onOpenHealth = { openTab(TopLevelDestination.HEALTH) }
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(modifier: Modifier = Modifier, title: String) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title + "\n" + stringResource(R.string.placeholder_coming_soon),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// What the screens behind the sign-in see while nobody is signed in (they are not shown then)
private val SIGNED_OUT = CareContext(userAccountId = "", userFullName = "", careRecipientProfileId = "", careRecipientName = "", callNumber = null)
