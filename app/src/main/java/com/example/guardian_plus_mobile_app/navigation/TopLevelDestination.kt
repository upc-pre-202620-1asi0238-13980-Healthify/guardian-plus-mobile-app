package com.example.guardian_plus_mobile_app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.navigation.AlertsRoute
import kotlinx.serialization.Serializable

// Tabs whose bounded contexts are not built yet; each team replaces its route with its own nav graph
@Serializable
object HomeRoute

@Serializable
object HealthRoute

@Serializable
object RoutinesRoute

@Serializable
object LocationRoute

/** The five destinations of the bottom navigation bar, in the order of the team prototype. */
enum class TopLevelDestination(
    val route: Any,
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val labelRes: Int
) {
    HOME(HomeRoute, R.drawable.ic_house, R.string.nav_home),
    HEALTH(HealthRoute, R.drawable.ic_activity, R.string.nav_health),
    ALERTS(AlertsRoute, R.drawable.ic_bell, R.string.nav_alerts),
    ROUTINES(RoutinesRoute, R.drawable.ic_clipboard_check, R.string.nav_routines),
    LOCATION(LocationRoute, R.drawable.ic_map_pin, R.string.nav_location)
}
