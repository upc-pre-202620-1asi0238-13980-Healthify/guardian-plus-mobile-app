package com.example.guardian_plus_mobile_app.features.profile.presentation.carerecipient

data class CareRecipientUiState(
    val careRecipientProfileId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val relationshipLabel: String = "",
    val isRelationshipActive: Boolean = false,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}