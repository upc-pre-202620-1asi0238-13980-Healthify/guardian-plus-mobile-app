package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.HealthReportScreen
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.health.HealthScreen
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object HealthRoute

@Serializable
data class HealthReportRoute(val reportId: String)

/**
 * Routes of Health Monitoring. Switching bottom-bar tabs needs the shell's back stack rules, so the
 * shell passes how to open them instead of this graph navigating there itself.
 */
fun NavGraphBuilder.healthMonitoringNavGraph(
    navController: NavController,
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

    composable<HealthReportRoute> { backStackEntry ->
        val route: HealthReportRoute = backStackEntry.toRoute()
        HealthReportScreen(
            reportId = route.reportId,
            onBackClick = { navController.popBackStack() },
            onOpenReport = { reportId -> navController.navigate(HealthReportRoute(reportId = reportId)) }
        )
    }
}
