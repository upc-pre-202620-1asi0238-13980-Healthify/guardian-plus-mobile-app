package com.example.guardian_plus_mobile_app.features.auth.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.guardian_plus_mobile_app.features.auth.presentation.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

/**
 * Routes of Auth. The login is a mock that accepts any filled-in credentials; where it leads afterwards is
 * the shell's decision, so the shell passes it in.
 */
fun NavGraphBuilder.authNavGraph(onLoginSuccess: () -> Unit) {
    composable<LoginRoute> {
        LoginScreen(onLoginSuccess = onLoginSuccess)
    }
}
