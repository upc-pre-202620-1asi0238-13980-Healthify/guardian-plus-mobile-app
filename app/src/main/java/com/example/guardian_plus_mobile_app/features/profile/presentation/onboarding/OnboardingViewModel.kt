package com.example.guardian_plus_mobile_app.features.profile.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.profile.application.CareContextLoad
import com.example.guardian_plus_mobile_app.features.profile.application.CareRecipientData
import com.example.guardian_plus_mobile_app.features.profile.application.CompleteOnboardingUseCase
import com.example.guardian_plus_mobile_app.features.profile.application.OnboardingData
import com.example.guardian_plus_mobile_app.features.profile.application.UserProfileData
import com.example.guardian_plus_mobile_app.features.profile.domain.model.RelationshipType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val completeOnboarding: CompleteOnboardingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private var started = false

    /** Shows only the missing sections and fills the member's name typed at sign-up, if known. */
    fun start(needsUserProfile: Boolean, needsCareRecipient: Boolean, suggestedFullName: String?) {
        if (started) return
        started = true
        val words = suggestedFullName.orEmpty().trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        _uiState.update {
            it.copy(
                showUserProfile = needsUserProfile,
                showCareRecipient = needsCareRecipient,
                // "Juan Pablo Arias Díaz": the last two words are usually the surnames
                firstName = words.dropLast(minOf(2, words.size - 1).coerceAtLeast(0)).joinToString(" "),
                lastName = words.takeLast(minOf(2, words.size - 1).coerceAtLeast(0)).joinToString(" ")
            )
        }
    }

    fun onFirstNameChange(value: String) = _uiState.update { it.copy(firstName = value, errorMessage = null) }

    fun onLastNameChange(value: String) = _uiState.update { it.copy(lastName = value, errorMessage = null) }

    fun onPhoneNumberChange(value: String) = _uiState.update { it.copy(phoneNumber = value, errorMessage = null) }

    fun onCareRecipientFirstNameChange(value: String) =
        _uiState.update { it.copy(careRecipientFirstName = value, errorMessage = null) }

    fun onCareRecipientLastNameChange(value: String) =
        _uiState.update { it.copy(careRecipientLastName = value, errorMessage = null) }

    fun onCareRecipientBirthDateChange(value: LocalDate) =
        _uiState.update { it.copy(careRecipientBirthDate = value, errorMessage = null) }

    fun onRelationshipTypeChange(value: RelationshipType) = _uiState.update { it.copy(relationshipType = value) }

    fun submit(userAccountId: String) {
        val state = _uiState.value
        if (state.isLoading) return
        val error = validate(state)
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }
        val data = OnboardingData(
            userProfile = if (state.showUserProfile) {
                UserProfileData(state.firstName.trim(), state.lastName.trim(), state.phoneNumber.filterNot(Char::isWhitespace))
            } else {
                null
            },
            careRecipient = if (state.showCareRecipient) {
                CareRecipientData(
                    state.careRecipientFirstName.trim(),
                    state.careRecipientLastName.trim(),
                    requireNotNull(state.careRecipientBirthDate),
                    state.relationshipType
                )
            } else {
                null
            }
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            completeOnboarding(userAccountId, data)
                .onSuccess { load ->
                    if (load is CareContextLoad.Ready) {
                        _uiState.update { it.copy(isLoading = false, isCompleted = true) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudo completar tu perfil") }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo completar tu perfil") }
                }
        }
    }

    fun onNavigationHandled() = _uiState.update { it.copy(isCompleted = false) }

    private fun validate(state: OnboardingUiState): String? {
        if (state.showUserProfile) {
            if (state.firstName.isBlank() || state.lastName.isBlank() || state.phoneNumber.isBlank()) {
                return "Completa tus datos para continuar"
            }
            if (!PHONE_REGEX.matches(state.phoneNumber.filterNot(Char::isWhitespace))) {
                return "Ingresa un celular válido, por ejemplo +51987654321"
            }
        }
        if (state.showCareRecipient) {
            if (state.careRecipientFirstName.isBlank() || state.careRecipientLastName.isBlank()) {
                return "Ingresa el nombre de la persona que cuidas"
            }
            val birthDate = state.careRecipientBirthDate ?: return "Selecciona su fecha de nacimiento"
            if (birthDate.isAfter(LocalDate.now())) return "La fecha de nacimiento no puede estar en el futuro"
        }
        return null
    }

    private companion object {
        // Optional + and 9 to 15 digits, as E.164 numbers have
        val PHONE_REGEX = Regex("^\\+?\\d{9,15}$")
    }
}
