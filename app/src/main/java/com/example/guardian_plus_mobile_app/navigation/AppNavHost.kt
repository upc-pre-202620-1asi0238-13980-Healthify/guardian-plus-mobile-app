package com.example.guardian_plus_mobile_app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.navigation.emergencyAlertingNavGraph
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.navigation.HomeRoute
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.navigation.healthMonitoringNavGraph

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    // The bar is only shown on the five main screens; details and full-screen alerts hide it
    val currentDestination = TopLevelDestination.entries.firstOrNull { destination ->
        backStackEntry?.destination?.hasRoute(destination.route::class) == true
    }
    val openTab: (TopLevelDestination) -> Unit = { destination ->
        navController.navigate(destination.route) {
            // Each tab keeps its own state (report, navigation rules)
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentDestination != null) {
                GuardianBottomBar(
                    selected = currentDestination,
                    onDestinationClick = openTab
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            // The prototype opens on "Inicio"
            startDestination = HomeRoute,
            modifier = Modifier.padding(paddingValues)
        ) {
            healthMonitoringNavGraph(
                onOpenHealth = { openTab(TopLevelDestination.HEALTH) },
                onOpenAlerts = { openTab(TopLevelDestination.ALERTS) },
                onOpenLocation = { openTab(TopLevelDestination.LOCATION) }
            )
            composable<RoutinesRoute> { PlaceholderScreen(title = stringResource(R.string.nav_routines)) }
            composable<LocationRoute> { PlaceholderScreen(title = stringResource(R.string.nav_location)) }
            emergencyAlertingNavGraph(
                navController = navController,
                onOpenLocation = { openTab(TopLevelDestination.LOCATION) },
                onOpenHealth = { openTab(TopLevelDestination.HEALTH) }
            )
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
