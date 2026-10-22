package com.example.guardian_plus_mobile_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.auth.application.GetCurrentSessionUseCase
import com.example.guardian_plus_mobile_app.features.auth.presentation.navigation.LoginRoute
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.navigation.HomeRoute
import com.example.guardian_plus_mobile_app.features.profile.presentation.navigation.SessionStartRoute
import com.example.guardian_plus_mobile_app.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var getCurrentSession: GetCurrentSessionUseCase

    @Inject
    lateinit var careContextStore: CareContextStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Read once: a session that expires later is dropped by the network layer, not by navigation
        val startDestination: Any = when {
            getCurrentSession() == null -> LoginRoute
            // The device already knows whom this account cares for
            careContextStore.current() != null -> HomeRoute
            else -> SessionStartRoute
        }
        setContent {
            val careContext by careContextStore.context.collectAsStateWithLifecycle()
            GuardianTheme {
                AppNavHost(
                    navController = rememberNavController(),
                    startDestination = startDestination,
                    careContext = careContext
                )
            }
        }
    }
}
