package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

interface AlertChannelSettingRepository {
    /** Only the channels the member configured; a member with none receives in-app alerts. */
    suspend fun getChannelSettings(userId: String): Result<List<AlertChannelSetting>>
    suspend fun configureChannel(userId: String, channel: NotificationChannel, enabled: Boolean): Result<AlertChannelSetting>
    /** Turns PUSH on with the token of this phone, so the platform delivers alerts to it. */
    suspend fun registerPushDevice(userId: String, deviceToken: String): Result<AlertChannelSetting>
}
