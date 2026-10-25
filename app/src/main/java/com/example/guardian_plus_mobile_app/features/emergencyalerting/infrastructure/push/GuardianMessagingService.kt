package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.push

import androidx.core.app.NotificationManagerCompat
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.RegisterPushDeviceUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Receives Firebase Cloud Messaging events for this phone. */
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

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
