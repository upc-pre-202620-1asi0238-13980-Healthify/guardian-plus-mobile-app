package com.example.guardian_plus_mobile_app.features.profile.presentation.personaldata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.auth.application.GetCurrentUserAccountUseCase
import com.example.guardian_plus_mobile_app.features.profile.domain.model.RelationshipType
import com.example.guardian_plus_mobile_app.features.profile.domain.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PersonalDataViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val getCurrentUserAccount: GetCurrentUserAccountUseCase,
    private val careContextStore: CareContextStore
) : ViewModel() {

    private val careContext = careContextStore.require()

    private val _uiState = MutableStateFlow(PersonalDataUiState())
    val uiState: StateFlow<PersonalDataUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun onFirstNameChange(value: String) {
        _uiState.update {
            it.copy(
                firstName = value,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun onLastNameChange(value: String) {
        _uiState.update {
            it.copy(
                lastName = value,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun onPhoneNumberChange(value: String) {
        _uiState.update {
            it.copy(
                phoneNumber = value,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun save() {
        val state = _uiState.value

        if (state.isSaving) return

        val firstName = state.firstName.trim()
        val lastName = state.lastName.trim()
        val phoneNumber = state.phoneNumber.filterNot(Char::isWhitespace)

        val validationError = validate(
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber
        )

        if (validationError != null) {
            _uiState.update {
                it.copy(errorMessage = validationError)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            runCatching {
                profileRepository.updateUserProfile(
                    userProfileId = state.userProfileId,
                    firstName = firstName,
                    lastName = lastName
                ).getOrThrow()

                profileRepository.updateContactInformation(
                    userProfileId = state.userProfileId,
                    phoneNumber = phoneNumber
                ).getOrThrow()

                val currentContext = careContextStore.require()

                careContextStore.save(
                    currentContext.copy(
                        userFullName = "$firstName $lastName"
                    )
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        firstName = firstName,
                        lastName = lastName,
                        phoneNumber = phoneNumber,
                        isSaving = false,
                        successMessage = "Cambios guardados correctamente"
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = exception.message
                            ?: "No se pudieron guardar los cambios"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                val account = getCurrentUserAccount().getOrThrow()

                val profile = profileRepository
                    .findUserProfile(account.id)
                    .getOrThrow()
                    ?: error("No se encontró el perfil del usuario")

                val relationship = profileRepository
                    .getCareRelationships(account.id)
                    .getOrThrow()
                    .firstOrNull {
                        it.active &&
                                it.careRecipientProfileId == careContext.careRecipientProfileId
                    }

                PersonalDataUiState(
                    userProfileId = profile.id,
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    email = account.email.value,
                    phoneNumber = profile.phoneNumber,
                    relationshipLabel = when (relationship?.relationshipType) {
                        RelationshipType.FAMILY -> "Familiar"
                        RelationshipType.CAREGIVER -> "Cuidador"
                        null -> ""
                    },
                    careRecipientName = careContext.careRecipientName,
                    isLoading = false
                )
            }.onSuccess { loadedState ->
                _uiState.value = loadedState
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message
                            ?: "No se pudieron cargar tus datos"
                    )
                }
            }
        }
    }

    private fun validate(
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): String? {
        if (firstName.isBlank()) {
            return "Ingresa tu nombre"
        }

        if (lastName.isBlank()) {
            return "Ingresa tus apellidos"
        }

        if (phoneNumber.isBlank()) {
            return "Ingresa tu número telefónico"
        }

        if (!PHONE_REGEX.matches(phoneNumber)) {
            return "Ingresa un celular válido, por ejemplo +51987654321"
        }

        return null
    }

    private companion object {
        val PHONE_REGEX = Regex("^\\+?\\d{9,15}$")
    }
}