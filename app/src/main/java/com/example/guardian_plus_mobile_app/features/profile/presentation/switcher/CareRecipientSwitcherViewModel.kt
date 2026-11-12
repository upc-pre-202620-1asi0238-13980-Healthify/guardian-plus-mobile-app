package com.example.guardian_plus_mobile_app.features.profile.presentation.switcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.profile.application.GetCareRecipientsUseCase
import com.example.guardian_plus_mobile_app.features.profile.application.SwitchCareRecipientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** "Cambiar persona": everyone the signed-in account cares for, and the switch to another of them. */
@HiltViewModel
class CareRecipientSwitcherViewModel @Inject constructor(
    private val getCareRecipients: GetCareRecipientsUseCase,
    private val switchCareRecipient: SwitchCareRecipientUseCase,
    private val careContextStore: CareContextStore
) : ViewModel() {

    private val careContext = careContextStore.require()

    private val _uiState = MutableStateFlow(
        CareRecipientSwitcherUiState(currentCareRecipientId = careContext.careRecipientProfileId)
    )
    val uiState: StateFlow<CareRecipientSwitcherUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getCareRecipients(careContext.userAccountId)
                .onSuccess { careRecipients ->
                    _uiState.update { it.copy(isLoading = false, careRecipients = careRecipients) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar la lista") }
                }
        }
    }

    fun select(careRecipientProfileId: String) {
        val state = _uiState.value
        if (state.switchingToId != null || careRecipientProfileId == state.currentCareRecipientId) return
        viewModelScope.launch {
            _uiState.update { it.copy(switchingToId = careRecipientProfileId, errorMessage = null) }
            switchCareRecipient(careRecipientProfileId)
                .onSuccess { _uiState.update { it.copy(switchingToId = null, isSwitched = true) } }
                .onFailure { e ->
                    _uiState.update { it.copy(switchingToId = null, errorMessage = e.message ?: "No se pudo cambiar de persona") }
                }
        }
    }
}
