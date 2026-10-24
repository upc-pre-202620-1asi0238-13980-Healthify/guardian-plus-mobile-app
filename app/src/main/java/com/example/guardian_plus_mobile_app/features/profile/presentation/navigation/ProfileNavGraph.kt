package com.example.guardian_plus_mobile_app.features.profile.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.guardian_plus_mobile_app.features.profile.presentation.home.ProfileHomeScreen
import com.example.guardian_plus_mobile_app.features.profile.presentation.onboarding.OnboardingScreen
import com.example.guardian_plus_mobile_app.features.profile.presentation.start.SessionStartDestination
import com.example.guardian_plus_mobile_app.features.profile.presentation.start.SessionStartScreen
import com.example.guardian_plus_mobile_app.features.profile.presentation.personaldata.PersonalDataScreen
import com.example.guardian_plus_mobile_app.features.profile.presentation.carerecipient.CareRecipientScreen
import com.example.guardian_plus_mobile_app.features.profile.presentation.carecircle.CareCircleScreen
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.wearable.WearableDetailsScreen
import com.example.guardian_plus_mobile_app.features.profile.presentation.accessibility.AccessibilityScreen
import com.example.guardian_plus_mobile_app.features.profile.presentation.language.LanguageScreen
import com.example.guardian_plus_mobile_app.features.profile.presentation.settings.SettingsScreen
import com.example.guardian_plus_mobile_app.features.profile.presentation.about.AboutScreen
import com.example.guardian_plus_mobile_app.features.profile.presentation.terms.TermsScreen
import com.example.guardian_plus_mobile_app.features.subscriptions.presentation.myplan.MyPlanScreen
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

@Serializable
object ProfileHomeRoute
@Serializable
object PersonalDataRoute

@Serializable
object CareRecipientRoute
@Serializable
object CareCircleRoute
@Serializable
object WearableDetailsRoute
@Serializable
object SettingsRoute

@Serializable
object LanguageRoute

@Serializable
object AccessibilityRoute
@Serializable
object TermsRoute

@Serializable
object AboutRoute
@Serializable
object MyPlanRoute
/**
 * Routes between the sign-in and Home. Where each outcome leads is the shell's decision, so the shell passes it in.
 */
fun NavGraphBuilder.profileNavGraph(
    navController: NavHostController,
    onReady: () -> Unit,
    onOnboardingRequired: (OnboardingRoute) -> Unit,
    onSignedOut: () -> Unit,
    onLogout: () -> Unit
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


    composable<ProfileHomeRoute> {
        ProfileHomeScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onPersonalDataClick = {
                navController.navigate(
                    PersonalDataRoute
                ) {
                    launchSingleTop = true
                }
            },
            onCareRecipientClick = {
                navController.navigate(
                    CareRecipientRoute
                ) {
                    launchSingleTop = true
                }
            },
            onCareCircleClick = {
                navController.navigate(CareCircleRoute) {
                    launchSingleTop = true
                }
            },
            onWearableClick = {
                navController.navigate(WearableDetailsRoute) {
                    launchSingleTop = true
                }
            },
            onSettingsClick = {
                navController.navigate(SettingsRoute) {
                    launchSingleTop = true
                }
            },
            onPlanClick = {
                navController.navigate(MyPlanRoute) {
                    launchSingleTop = true
                }
            },
            onLogout = onLogout
        )
    }

    composable<PersonalDataRoute> {
        PersonalDataScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable<CareRecipientRoute> {
        CareRecipientScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
    composable<CareCircleRoute> {
        CareCircleScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
    composable<WearableDetailsRoute> {
        WearableDetailsScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
    composable<SettingsRoute> {
        SettingsScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onLanguageClick = {
                navController.navigate(LanguageRoute) {
                    launchSingleTop = true
                }
            },
            onAccessibilityClick = {
                navController.navigate(AccessibilityRoute) {
                    launchSingleTop = true
                }
            },
            onTermsClick = {
                navController.navigate(TermsRoute) {
                    launchSingleTop = true
                }
            },
            onAboutClick = {
                navController.navigate(AboutRoute) {
                    launchSingleTop = true
                }
            }
        )
    }
    composable<LanguageRoute> {
        LanguageScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable<AccessibilityRoute> {
        AccessibilityScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
    composable<TermsRoute> {
        TermsScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable<AboutRoute> {
        AboutScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onTermsClick = {
                navController.navigate(TermsRoute) {
                    launchSingleTop = true
                }
            }
        )
    }
    composable<MyPlanRoute> {
        MyPlanScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}