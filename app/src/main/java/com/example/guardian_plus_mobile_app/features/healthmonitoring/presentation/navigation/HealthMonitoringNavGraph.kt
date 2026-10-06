package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.health.HealthScreen
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object HealthRoute

/**
 * Routes of Health Monitoring. Switching bottom-bar tabs needs the shell's back stack rules, so the
 * shell passes how to open them instead of this graph navigating there itself.
 */
fun NavGraphBuilder.healthMonitoringNavGraph(
    onOpenHealth: () -> Unit,
    onOpenAlerts: () -> Unit,
    onOpenLocation: () -> Unit
) {
    composable<HomeRoute> {
        HomeScreen(onSeeVitalsClick = onOpenHealth, onLocationClick = onOpenLocation, onAlertsClick = onOpenAlerts)
    }

    composable<HealthRoute> {
        HealthScreen()
    }
}
