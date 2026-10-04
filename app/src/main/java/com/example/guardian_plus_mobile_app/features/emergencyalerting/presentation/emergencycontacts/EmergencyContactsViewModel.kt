package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.emergencycontacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.AddEmergencyContactUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetAlertSettingsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetEmergencyContactsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.RemoveEmergencyContactUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.ReorderEmergencyContactsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EmergencyContactsViewModel @Inject constructor(
    private val getEmergencyContacts: GetEmergencyContactsUseCase,
    private val getAlertSettings: GetAlertSettingsUseCase,
    private val addEmergencyContact: AddEmergencyContactUseCase,
    private val removeEmergencyContact: RemoveEmergencyContactUseCase,
    private val reorderEmergencyContacts: ReorderEmergencyContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        EmergencyContactsUiState(careRecipientFirstName = DemoSession.CARE_RECIPIENT_FIRST_NAME)
    )
    val uiState: StateFlow<EmergencyContactsUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getAlertSettings(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .onSuccess { settings -> _uiState.update { it.copy(ackTimeoutSec = settings.primaryAckTimeoutSec) } }
            getEmergencyContacts(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .onSuccess { contacts ->
                    _uiState.update { it.copy(isLoading = false, contacts = contacts.activeInOrder()) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudieron cargar los contactos") }
                }
        }
    }

    /** Moves a contact one place up (-1) or down (+1) in the escalation chain. */
    fun move(contactId: String, offset: Int) {
        val state = _uiState.value
        if (state.isSaving) return
        val from = state.contacts.indexOfFirst { it.id == contactId }
        val to = from + offset
        if (from == -1 || to !in state.contacts.indices) return

        val previous = state.contacts
        val reordered = previous.toMutableList().apply { add(to, removeAt(from)) }
        // The new order shows at once; if the platform rejects it, the previous one comes back
        _uiState.update { it.copy(contacts = reordered, isSaving = true) }
        viewModelScope.launch {
            reorderEmergencyContacts(DemoSession.CARE_RECIPIENT_PROFILE_ID, reordered.map { it.id })
                .onSuccess { saved -> _uiState.update { it.copy(isSaving = false, contacts = saved.activeInOrder()) } }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isSaving = false, contacts = previous, actionErrorMessage = e.message ?: "No se pudo cambiar el orden")
                    }
                }
        }
    }

    fun requestRemoval(contact: EmergencyContact) {
        _uiState.update { it.copy(contactPendingRemoval = contact) }
    }

    fun cancelRemoval() {
        _uiState.update { it.copy(contactPendingRemoval = null) }
    }

    fun confirmRemoval() {
        val contact = _uiState.value.contactPendingRemoval ?: return
        _uiState.update { it.copy(contactPendingRemoval = null, isSaving = true) }
        viewModelScope.launch {
            removeEmergencyContact(contact.id)
                .onSuccess { reloadAfterChange() }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isSaving = false, actionErrorMessage = e.message ?: "No se pudo eliminar el contacto")
                    }
                }
        }
    }

    fun openAddSheet() {
        _uiState.update { it.copy(isAddSheetOpen = true) }
    }

    fun closeAddSheet() {
        _uiState.update { it.copy(isAddSheetOpen = false) }
    }

    fun addContact(displayName: String, relationship: String, phoneNumber: String) {
        if (_uiState.value.isAdding) return
        viewModelScope.launch {
            _uiState.update { it.copy(isAdding = true) }
            addEmergencyContact(DemoSession.CARE_RECIPIENT_PROFILE_ID, displayName, relationship, phoneNumber)
                .onSuccess {
                    _uiState.update { it.copy(isAdding = false, isAddSheetOpen = false) }
                    reloadAfterChange()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isAdding = false, actionErrorMessage = e.message ?: "No se pudo agregar el contacto")
                    }
                }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorMessage = null) }
    }

    // Adding or removing changes the priorities of the others, so the list is read again
    private suspend fun reloadAfterChange() {
        getEmergencyContacts(DemoSession.CARE_RECIPIENT_PROFILE_ID)
            .onSuccess { contacts -> _uiState.update { it.copy(isSaving = false, contacts = contacts.activeInOrder()) } }
            .onFailure { _uiState.update { it.copy(isSaving = false) } }
    }

    private fun List<EmergencyContact>.activeInOrder(): List<EmergencyContact> =
        filter { it.active }.sortedBy { it.priorityOrder }
}
