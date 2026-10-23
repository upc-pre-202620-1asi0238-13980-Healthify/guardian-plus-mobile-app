package com.example.guardian_plus_mobile_app.features.profile.presentation.personaldata

data class PersonalDataUiState(
    val userProfileId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val relationshipLabel: String = "",
    val careRecipientName: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}