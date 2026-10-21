package com.example.guardian_plus_mobile_app.features.auth.presentation.navigation

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.auth.presentation.LoginScreen
import com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset.ForgotPasswordScreen
import com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset.ResetPasswordScreen
import com.example.guardian_plus_mobile_app.features.auth.presentation.register.RegisterScreen
import com.example.guardian_plus_mobile_app.features.auth.presentation.twofactor.TwoFactorScreen
import com.example.guardian_plus_mobile_app.features.auth.presentation.verifyemail.VerifyEmailScreen
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

@Serializable
data class VerifyEmailRoute(val userAccountId: String, val email: String)

@Serializable
data class TwoFactorRoute(val userAccountId: String)

@Serializable
object ForgotPasswordRoute

@Serializable
object ResetPasswordRoute

/**
 * Routes of IAM: sign-in, sign-up with email verification, the two-factor challenge and password recovery.
 * Where a session leads afterwards is the shell's decision, so the shell passes it in.
 */
fun NavGraphBuilder.authNavGraph(navController: NavHostController, onLoginSuccess: () -> Unit) {
    // Verifying the email or resetting the password ends the flow back on the login
    val backToLogin = { navController.popBackStack<LoginRoute>(inclusive = false) }

    composable<LoginRoute> {
        LoginScreen(
            onLoginSuccess = onLoginSuccess,
            onSecondFactorRequired = { userAccountId -> navController.navigate(TwoFactorRoute(userAccountId)) },
            onEmailVerificationRequired = { userAccountId, email ->
                navController.navigate(VerifyEmailRoute(userAccountId, email))
            },
            onForgotPassword = { navController.navigate(ForgotPasswordRoute) },
            onRegister = { navController.navigate(RegisterRoute) }
        )
    }

    composable<RegisterRoute> {
        RegisterScreen(
            onBackClick = { navController.popBackStack() },
            onRegistered = { userAccountId, email ->
                // Back from the verification returns to the login, the account already exists
                navController.navigate(VerifyEmailRoute(userAccountId, email)) {
                    popUpTo<RegisterRoute> { inclusive = true }
                }
            }
        )
    }

    composable<VerifyEmailRoute> { backStackEntry ->
        val route: VerifyEmailRoute = backStackEntry.toRoute()
        val context = LocalContext.current
        VerifyEmailScreen(
            userAccountId = route.userAccountId,
            email = route.email,
            onBackClick = { navController.popBackStack() },
            onVerified = {
                Toast.makeText(context, R.string.auth_email_verified, Toast.LENGTH_LONG).show()
                backToLogin()
            }
        )
    }

    composable<TwoFactorRoute> { backStackEntry ->
        val route: TwoFactorRoute = backStackEntry.toRoute()
        TwoFactorScreen(
            userAccountId = route.userAccountId,
            onBackClick = { navController.popBackStack() },
            onAuthenticated = onLoginSuccess
        )
    }

    composable<ForgotPasswordRoute> {
        ForgotPasswordScreen(
            onBackClick = { navController.popBackStack() },
            onOpenResetPassword = { navController.navigate(ResetPasswordRoute) }
        )
    }

    composable<ResetPasswordRoute> {
        val context = LocalContext.current
        ResetPasswordScreen(
            onBackClick = { navController.popBackStack() },
            onPasswordReset = {
                Toast.makeText(context, R.string.auth_reset_password_done, Toast.LENGTH_LONG).show()
                backToLogin()
            }
        )
    }
}
