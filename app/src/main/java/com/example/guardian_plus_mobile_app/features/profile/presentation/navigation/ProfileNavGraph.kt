package com.example.guardian_plus_mobile_app.features.profile.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.guardian_plus_mobile_app.features.profile.presentation.onboarding.OnboardingScreen
import com.example.guardian_plus_mobile_app.features.profile.presentation.start.SessionStartDestination
import com.example.guardian_plus_mobile_app.features.profile.presentation.start.SessionStartScreen
import kotlinx.serialization.Serializable

/** Loads the care context of a session before entering the app. */
@Serializable
object SessionStartRoute

/** Asks for what Profile is missing about the signed-in account. */
@Serializable
data class OnboardingRoute(
    val userAccountId: String,
    val needsUserProfile: Boolean,
    val needsCareRecipient: Boolean,
    val suggestedFullName: String? = null
)

/**
 * Routes between the sign-in and Home. Where each outcome leads is the shell's decision, so the shell passes it in.
 */
fun NavGraphBuilder.profileNavGraph(
    onReady: () -> Unit,
    onOnboardingRequired: (OnboardingRoute) -> Unit,
    onSignedOut: () -> Unit
) {
    composable<SessionStartRoute> {
        SessionStartScreen(
            onDestination = { destination ->
                when (destination) {
                    SessionStartDestination.Home -> onReady()
                    SessionStartDestination.Login -> onSignedOut()
                    is SessionStartDestination.Onboarding -> onOnboardingRequired(
                        OnboardingRoute(
                            destination.userAccountId,
                            destination.needsUserProfile,
                            destination.needsCareRecipient,
                            destination.suggestedFullName
                        )
                    )
                }
            }
        )
    }

    composable<OnboardingRoute> { backStackEntry ->
        val route: OnboardingRoute = backStackEntry.toRoute()
        OnboardingScreen(
            userAccountId = route.userAccountId,
            needsUserProfile = route.needsUserProfile,
            needsCareRecipient = route.needsCareRecipient,
            suggestedFullName = route.suggestedFullName,
            onCompleted = onReady
        )
    }
}
