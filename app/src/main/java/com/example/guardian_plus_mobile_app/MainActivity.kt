package com.example.guardian_plus_mobile_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.network.LocalNetworkPermission
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.auth.application.GetCurrentSessionUseCase
import com.example.guardian_plus_mobile_app.features.auth.presentation.navigation.LoginRoute
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.push.ReminderNotifications
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.push.OpenAppointmentsFromNotification
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.push.AlertNotifications
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.push.OpenAlertFromNotification
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.push.PushRegistration
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

    // The alert of the push notification that opened the app, until its detail is shown
    private var notificationAlertId by mutableStateOf<String?>(null)

    // Whether the app was opened from an appointment reminder, until the appointments are shown
    private var openAppointmentsFromNotification by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // A recreated activity already handled the notification that started it
        if (savedInstanceState == null) {
            notificationAlertId = intent.alertId()
            openAppointmentsFromNotification = ReminderNotifications.isAppointmentReminder(intent)
        }
        // Read once: a session that expires later is dropped by the network layer, not by navigation
        val startDestination: Any = when {
            getCurrentSession() == null -> LoginRoute
            // The device already knows whom this account cares for
            careContextStore.current() != null -> HomeRoute
            else -> SessionStartRoute
        }
        setContent {
            val careContext by careContextStore.context.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            // The notification permission waits for the local network one: Android drops overlapping requests
            var localNetworkResolved by remember { mutableStateOf(false) }
            GuardianTheme {
                LocalNetworkPermission(onResolved = { localNetworkResolved = true })
                if (localNetworkResolved) PushRegistration(userAccountId = careContext?.userAccountId)
                AppNavHost(
                    navController = navController,
                    startDestination = startDestination,
                    careContext = careContext
                )
                OpenAlertFromNotification(
                    navController = navController,
                    alertId = notificationAlertId,
                    signedIn = careContext != null,
                    onOpened = { notificationAlertId = null }
                )
                OpenAppointmentsFromNotification(
                    navController = navController,
                    requested = openAppointmentsFromNotification,
                    signedIn = careContext != null,
                    onOpened = { openAppointmentsFromNotification = false }
                )
            }
        }
    }

    // A notification tapped while the app is already running (singleTop)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.alertId()?.let { notificationAlertId = it }
        if (ReminderNotifications.isAppointmentReminder(intent)) openAppointmentsFromNotification = true
    }

    private fun Intent.alertId(): String? = getStringExtra(AlertNotifications.ALERT_ID_EXTRA)
}
