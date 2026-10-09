package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.RoutinesScreen
import kotlinx.serialization.Serializable

@Serializable
object RoutinesRoute

/** Routes of Care Routines & Wellness. The detail screen of each routine will be added here. */
fun NavGraphBuilder.careRoutinesWellnessNavGraph() {
    composable<RoutinesRoute> {
        RoutinesScreen()
    }
}
