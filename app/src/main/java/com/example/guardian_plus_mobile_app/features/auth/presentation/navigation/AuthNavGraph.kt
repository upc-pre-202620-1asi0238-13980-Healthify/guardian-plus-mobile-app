package com.example.guardian_plus_mobile_app.features.auth.presentation.navigation

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.OtpPurpose
import com.example.guardian_plus_mobile_app.features.auth.presentation.LoginScreen
import com.example.guardian_plus_mobile_app.features.auth.presentation.otp.OtpVerificationScreen
import com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset.ForgotPasswordScreen
import com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset.ResetPasswordScreen
import com.example.guardian_plus_mobile_app.features.auth.presentation.register.RegisterScreen
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

/** Code IAM emailed to [email]: the email verification of a new account or the second factor of a sign-in. */
@Serializable
data class OtpVerificationRoute(val userAccountId: String, val email: String, val purpose: OtpPurpose)

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
            onSecondFactorRequired = { userAccountId, email ->
                navController.navigate(OtpVerificationRoute(userAccountId, email, OtpPurpose.LOGIN))
            },
            onEmailVerificationRequired = { userAccountId, email ->
                navController.navigate(OtpVerificationRoute(userAccountId, email, OtpPurpose.EMAIL_VERIFICATION))
            },
            onForgotPassword = { navController.navigate(ForgotPasswordRoute) },
            onRegister = { navController.navigate(RegisterRoute) }
        )
    }

    composable<RegisterRoute> {
        RegisterScreen(
            onLoginClick = { navController.popBackStack() },
            onRegistered = { userAccountId, email ->
                // Back from the verification returns to the login, the account already exists
                navController.navigate(OtpVerificationRoute(userAccountId, email, OtpPurpose.EMAIL_VERIFICATION)) {
                    popUpTo<RegisterRoute> { inclusive = true }
                }
            }
        )
    }

    composable<OtpVerificationRoute> { backStackEntry ->
        val route: OtpVerificationRoute = backStackEntry.toRoute()
        val context = LocalContext.current
        OtpVerificationScreen(
            userAccountId = route.userAccountId,
            email = route.email,
            purpose = route.purpose,
            onBackClick = { navController.popBackStack() },
            onVerified = {
                when (route.purpose) {
                    // The second factor completes the sign-in
                    OtpPurpose.LOGIN -> onLoginSuccess()
                    OtpPurpose.EMAIL_VERIFICATION -> {
                        Toast.makeText(context, R.string.auth_email_verified, Toast.LENGTH_LONG).show()
                        backToLogin()
                    }
                }
            }
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
