package com.example.guardian_plus_mobile_app.features.profile.presentation.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.auth.application.GetCurrentSessionUseCase
import com.example.guardian_plus_mobile_app.features.auth.application.LogoutUseCase
import com.example.guardian_plus_mobile_app.features.profile.application.CareContextLoad
import com.example.guardian_plus_mobile_app.features.profile.application.LoadCareContextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SessionStartUiState {
    data object Loading : SessionStartUiState

    data class Failed(val message: String) : SessionStartUiState

    /** Where to go next; the screen navigates and the state is not shown again. */
    data class Done(val next: SessionStartDestination) : SessionStartUiState
}

sealed interface SessionStartDestination {
    data object Home : SessionStartDestination

    data object Login : SessionStartDestination

    data class Onboarding(
        val userAccountId: String,
        val needsUserProfile: Boolean,
        val needsCareRecipient: Boolean,
        val suggestedFullName: String?
    ) : SessionStartDestination
}

/** Loads the care context of the session that was just opened (or restored) before entering the app. */
@HiltViewModel
class SessionStartViewModel @Inject constructor(
    private val loadCareContext: LoadCareContextUseCase,
    private val getCurrentSession: GetCurrentSessionUseCase,
    private val logout: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SessionStartUiState>(SessionStartUiState.Loading)
    val uiState: StateFlow<SessionStartUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { SessionStartUiState.Loading }
            loadCareContext()
                .onSuccess { load ->
                    val next = when (load) {
                        is CareContextLoad.Ready -> SessionStartDestination.Home
                        is CareContextLoad.OnboardingRequired -> SessionStartDestination.Onboarding(
                            load.userAccountId, load.needsUserProfile, load.needsCareRecipient, load.suggestedFullName
                        )
                    }
                    _uiState.update { SessionStartUiState.Done(next) }
                }
                .onFailure { e ->
                    // A token the platform rejected is dropped on the way back: there is no session to load anymore
                    if (getCurrentSession() == null) {
                        logout()
                        _uiState.update { SessionStartUiState.Done(SessionStartDestination.Login) }
                    } else {
                        _uiState.update { SessionStartUiState.Failed(e.message ?: "No se pudo cargar tu cuenta") }
                    }
                }
        }
    }

    fun signOut() {
        logout()
        _uiState.update { SessionStartUiState.Done(SessionStartDestination.Login) }
    }
}
