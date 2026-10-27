package com.example.guardian_plus_mobile_app

import android.app.Application
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.push.AlertNotifications
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GuardianApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Before any push arrives: a message names a channel that must already exist
        AlertNotifications.registerChannels(this)
    }
}
