package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertsettings

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSetting
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettings
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.NotificationChannel

data class AlertSettingsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val settings: AlertSettings? = null,
    // Only what the member configured; see enabledChannels for what is actually in effect
    val configuredChannels: List<AlertChannelSetting> = emptyList(),
    val contacts: List<EmergencyContact> = emptyList(),
    val isSavingSettings: Boolean = false,
    val savingChannel: NotificationChannel? = null,
    val actionErrorMessage: String? = null
) {
    /** A member who never configured a channel receives in-app alerts, as the platform does. */
    val enabledChannels: Set<NotificationChannel>
        get() = if (configuredChannels.isEmpty()) {
            setOf(NotificationChannel.IN_APP)
        } else {
            configuredChannels.filter { it.enabled }.map { it.channel }.toSet()
        }

    /** The platform refuses to turn off the last enabled channel. */
    fun canDisable(channel: NotificationChannel): Boolean =
        channel !in enabledChannels || enabledChannels.size > 1

    val primaryContact: EmergencyContact? get() = contacts.minByOrNull { it.priorityOrder }
}
