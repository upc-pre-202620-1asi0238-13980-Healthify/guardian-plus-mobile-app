package com.example.guardian_plus_mobile_app.features.profile.presentation.onboarding

import com.example.guardian_plus_mobile_app.features.profile.domain.model.RelationshipType
import java.time.LocalDate

/** Fields of the onboarding; each section only shows when the account is missing that part. */
data class OnboardingUiState(
    val showUserProfile: Boolean = true,
    val showCareRecipient: Boolean = true,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val careRecipientFirstName: String = "",
    val careRecipientLastName: String = "",
    val careRecipientBirthDate: LocalDate? = null,
    val relationshipType: RelationshipType = RelationshipType.FAMILY,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Set once the care context is ready; the screen enters the app and clears it
    val isCompleted: Boolean = false
)
