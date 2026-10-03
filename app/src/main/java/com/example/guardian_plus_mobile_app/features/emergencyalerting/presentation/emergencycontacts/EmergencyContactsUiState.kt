package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.emergencycontacts

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact

data class EmergencyContactsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Active contacts in escalation order: the first one is the primary contact
    val contacts: List<EmergencyContact> = emptyList(),
    val ackTimeoutSec: Int = 60,
    val careRecipientFirstName: String = "",
    // A reorder or a removal is being saved; the list ignores further changes meanwhile
    val isSaving: Boolean = false,
    val isAddSheetOpen: Boolean = false,
    val isAdding: Boolean = false,
    val contactPendingRemoval: EmergencyContact? = null,
    val actionErrorMessage: String? = null
) {
    /** The platform keeps at least one active contact, so the last one cannot be removed. */
    val canRemove: Boolean get() = contacts.size > 1
}
