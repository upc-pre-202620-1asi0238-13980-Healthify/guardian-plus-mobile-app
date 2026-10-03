package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.ConfigureAlertChannelUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetAlertChannelSettingsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetAlertSettingsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetEmergencyContactsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.SetSilentModeUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.UpdateAlertSettingsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettings
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.NotificationChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AlertSettingsViewModel @Inject constructor(
    private val getAlertSettings: GetAlertSettingsUseCase,
    private val updateAlertSettings: UpdateAlertSettingsUseCase,
    private val setSilentMode: SetSilentModeUseCase,
    private val getAlertChannelSettings: GetAlertChannelSettingsUseCase,
    private val configureAlertChannel: ConfigureAlertChannelUseCase,
    private val getEmergencyContacts: GetEmergencyContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertSettingsUiState())
    val uiState: StateFlow<AlertSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getAlertChannelSettings(DemoSession.CURRENT_USER_ID)
                .onSuccess { channels -> _uiState.update { it.copy(configuredChannels = channels) } }
            refreshContacts()
            getAlertSettings(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .onSuccess { settings -> _uiState.update { it.copy(isLoading = false, settings = settings) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar la configuración") }
                }
        }
    }

    /** The contacts row summarizes the list, which may have changed on the contacts screen. */
    fun onScreenShown() {
        viewModelScope.launch { refreshContacts() }
    }

    fun setEscalationEnabled(enabled: Boolean) = saveSettings { it.copy(escalationEnabled = enabled) }

    fun setAckTimeout(seconds: Int) = saveSettings { it.copy(primaryAckTimeoutSec = seconds) }

    fun setBroadcastCriticalImmediately(enabled: Boolean) = saveSettings { it.copy(broadcastCriticalImmediately = enabled) }

    fun setSilentModeEnabled(enabled: Boolean) {
        val previous = _uiState.value.settings ?: return
        _uiState.update { it.copy(settings = previous.copy(silentModeEnabled = enabled), isSavingSettings = true) }
        viewModelScope.launch {
            setSilentMode(DemoSession.CARE_RECIPIENT_PROFILE_ID, enabled)
                .onSuccess { saved -> _uiState.update { it.copy(settings = saved, isSavingSettings = false) } }
                .onFailure { e -> revertSettings(previous, e) }
        }
    }

    fun setChannelEnabled(channel: NotificationChannel, enabled: Boolean) {
        val state = _uiState.value
        if (state.savingChannel != null) return
        // Same rule as the platform, explained before sending a request that would be rejected
        if (!enabled && !state.canDisable(channel)) {
            _uiState.update { it.copy(actionErrorMessage = "Debe quedar al menos un canal activo para recibir las alertas") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(savingChannel = channel) }
            configureAlertChannel(DemoSession.CURRENT_USER_ID, channel, enabled)
                .onSuccess { saved ->
                    _uiState.update { state ->
                        val others = state.configuredChannels.filterNot { it.channel == saved.channel }
                        state.copy(configuredChannels = others + saved, savingChannel = null)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(savingChannel = null, actionErrorMessage = e.message ?: "No se pudo cambiar el canal")
                    }
                }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorMessage = null) }
    }

    /**
     * The platform updates timeout, escalation and broadcast together, so every change sends the three.
     * The switch moves at once and goes back if the platform rejects the change.
     */
    private fun saveSettings(change: (AlertSettings) -> AlertSettings) {
        val previous = _uiState.value.settings ?: return
        val updated = change(previous)
        _uiState.update { it.copy(settings = updated, isSavingSettings = true) }
        viewModelScope.launch {
            updateAlertSettings(
                DemoSession.CARE_RECIPIENT_PROFILE_ID,
                updated.primaryAckTimeoutSec,
                updated.escalationEnabled,
                updated.broadcastCriticalImmediately
            )
                .onSuccess { saved -> _uiState.update { it.copy(settings = saved, isSavingSettings = false) } }
                .onFailure { e -> revertSettings(previous, e) }
        }
    }

    private fun revertSettings(previous: AlertSettings, error: Throwable) {
        _uiState.update {
            it.copy(
                settings = previous,
                isSavingSettings = false,
                actionErrorMessage = error.message ?: "No se pudo guardar la configuración"
            )
        }
    }

    private suspend fun refreshContacts() {
        getEmergencyContacts(DemoSession.CARE_RECIPIENT_PROFILE_ID)
            .onSuccess { contacts -> _uiState.update { it.copy(contacts = contacts.filter { contact -> contact.active }) } }
    }
}
