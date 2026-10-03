package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.AlertDetailScreen
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerts.AlertsScreen
import kotlinx.serialization.Serializable

@Serializable
object AlertsRoute

@Serializable
data class AlertDetailRoute(val alertId: String)

/**
 * Routes of Emergency & Alerting. The Location and Health tabs belong to other bounded contexts, so
 * the app shell tells this graph how to open them instead of the feature knowing their routes.
 */
fun NavGraphBuilder.emergencyAlertingNavGraph(
    navController: NavController,
    onOpenLocation: () -> Unit,
    onOpenHealth: () -> Unit
) {

    composable<AlertsRoute> {
        AlertsScreen(onAlertClick = { alertId ->
            navController.navigate(AlertDetailRoute(alertId = alertId)) {
                // Acknowledging from the detail and coming back must not stack the same detail twice
                launchSingleTop = true
            }
        })
    }

    composable<AlertDetailRoute> { backStackEntry ->
        val route: AlertDetailRoute = backStackEntry.toRoute()
        AlertDetailScreen(
            alertId = route.alertId,
            onBackClick = { navController.popBackStack() },
            onViewLocationClick = onOpenLocation,
            onViewHealthClick = onOpenHealth
        )
    }
}
