package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.activity.ActivityScreen
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.appointmentform.AppointmentFormScreen
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.appointments.AppointmentsScreen
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.ROUTINE_RESULT_MESSAGE_KEY
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.hydration.HydrationScreen
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.inactivity.InactivityScreen
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.medication.MedicationScreen
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.medicationform.MedicationFormScreen
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.RoutineCategory
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.RoutinesScreen
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.sleep.SleepScreen
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.stock.StockScreen
import kotlinx.serialization.Serializable

@Serializable
object RoutinesRoute

@Serializable
object MedicationRoute

@Serializable
object MedicationFormRoute

@Serializable
object AppointmentsRoute

@Serializable
object AppointmentFormRoute

@Serializable
object ActivityRoute

@Serializable
object HydrationRoute

@Serializable
object SleepRoute

@Serializable
object InactivityRoute

@Serializable
object StockRoute

/** Routes of Care Routines & Wellness: the routines tab and the screen of each routine. */
fun NavGraphBuilder.careRoutinesWellnessNavGraph(navController: NavController) {
    val back: () -> Unit = { navController.popBackStack() }
    // A form goes back to the list below it and leaves it the confirmation to show
    val backWithMessage: (String) -> Unit = { message ->
        navController.previousBackStackEntry?.savedStateHandle?.set(ROUTINE_RESULT_MESSAGE_KEY, message)
        navController.popBackStack()
    }

    composable<RoutinesRoute> {
        RoutinesScreen(
            onCategoryClick = { category ->
                navController.navigate(category.route()) { launchSingleTop = true }
            }
        )
    }

    composable<MedicationRoute> {
        MedicationScreen(onBackClick = back, onAddDoseClick = { navController.navigate(MedicationFormRoute) })
    }

    composable<MedicationFormRoute> {
        MedicationFormScreen(onBackClick = back, onSaved = backWithMessage)
    }

    composable<AppointmentsRoute> {
        AppointmentsScreen(onBackClick = back, onAddAppointmentClick = { navController.navigate(AppointmentFormRoute) })
    }

    composable<AppointmentFormRoute> {
        AppointmentFormScreen(onBackClick = back, onSaved = backWithMessage)
    }

    composable<ActivityRoute> { ActivityScreen(onBackClick = back) }

    composable<HydrationRoute> { HydrationScreen(onBackClick = back) }

    composable<SleepRoute> { SleepScreen(onBackClick = back) }

    composable<InactivityRoute> { InactivityScreen(onBackClick = back) }

    composable<StockRoute> { StockScreen(onBackClick = back) }
}

private fun RoutineCategory.route(): Any = when (this) {
    RoutineCategory.MEDICATION -> MedicationRoute
    RoutineCategory.APPOINTMENTS -> AppointmentsRoute
    RoutineCategory.ACTIVITY -> ActivityRoute
    RoutineCategory.HYDRATION -> HydrationRoute
    RoutineCategory.SLEEP -> SleepRoute
    RoutineCategory.INACTIVITY -> InactivityRoute
    RoutineCategory.STOCK -> StockRoute
}
