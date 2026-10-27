package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.push

import androidx.core.app.NotificationManagerCompat
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.RegisterPushDeviceUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Receives Firebase Cloud Messaging events for this phone: token rotations and alerts. */
@AndroidEntryPoint
class GuardianMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var careContextStore: CareContextStore

    @Inject
    lateinit var registerPushDevice: RegisterPushDeviceUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // FCM rotates tokens now and then; the platform must learn the new one or alerts stop arriving
    override fun onNewToken(token: String) {
        val userId = careContextStore.current()?.userAccountId ?: return
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) return
        scope.launch { registerPushDevice(userId, token) }
    }

    // Only called while the app is in the foreground: in the background Android shows the notification itself
    override fun onMessageReceived(message: RemoteMessage) {
        val notification = message.notification ?: return
        AlertNotifications.show(
            context = this,
            alertId = message.data[AlertNotifications.ALERT_ID_EXTRA],
            title = notification.title,
            body = notification.body,
            audible = message.data["audible"] != "false"
        )
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
