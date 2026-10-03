package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

/** Whether a Care Circle member receives alerts through one channel (push, SMS or in-app). */
data class AlertChannelSetting(
    val userId: String,
    val channel: NotificationChannel,
    val enabled: Boolean,
    val deviceTokenRegistered: Boolean
)
