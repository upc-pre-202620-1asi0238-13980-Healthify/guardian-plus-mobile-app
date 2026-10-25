package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.push

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.RegisterPushDeviceUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.UnregisterPushDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Keeps this phone registered for push alerts while an account is signed in, and unregisters it on sign-out. */
@HiltViewModel
class PushRegistrationViewModel @Inject constructor(
    private val registerPushDevice: RegisterPushDeviceUseCase,
    private val unregisterPushDevice: UnregisterPushDeviceUseCase
) : ViewModel() {

    // Registered once per account and process; a failed attempt is retried the next time the account is seen
    private var registeredUserId: String? = null

    fun onSignedIn(userId: String) {
        if (registeredUserId == userId) return
        registeredUserId = userId
        viewModelScope.launch {
            registerPushDevice(userId).onFailure { if (registeredUserId == userId) registeredUserId = null }
        }
    }

    fun onSignedOut() {
        if (registeredUserId == null) return
        registeredUserId = null
        viewModelScope.launch { unregisterPushDevice() }
    }
}
