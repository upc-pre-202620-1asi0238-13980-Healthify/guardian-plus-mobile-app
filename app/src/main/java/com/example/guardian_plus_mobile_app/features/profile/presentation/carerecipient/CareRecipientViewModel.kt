package com.example.guardian_plus_mobile_app.features.profile.presentation.carerecipient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.profile.domain.model.RelationshipType
import com.example.guardian_plus_mobile_app.features.profile.domain.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CareRecipientViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val careContextStore: CareContextStore
) : ViewModel() {

    private val careContext = careContextStore.require()

    private val _uiState = MutableStateFlow(CareRecipientUiState())
    val uiState: StateFlow<CareRecipientUiState> = _uiState.asStateFlow()

    private var originalFirstName = ""
    private var originalLastName = ""
    private var originalBirthDate = ""

    init {
        load()
    }

    fun startEditing() {
        val state = _uiState.value

        originalFirstName = state.firstName
        originalLastName = state.lastName
        originalBirthDate = state.birthDate

        _uiState.update {
            it.copy(
                isEditing = true,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun cancelEditing() {
        _uiState.update {
            it.copy(
                firstName = originalFirstName,
                lastName = originalLastName,
                birthDate = originalBirthDate,
                isEditing = false,
                errorMessage = null
            )
        }
    }

    fun onFirstNameChange(value: String) {
        _uiState.update { it.copy(firstName = value) }
    }

    fun onLastNameChange(value: String) {
        _uiState.update { it.copy(lastName = value) }
    }

    fun onBirthDateChange(value: String) {
        _uiState.update { it.copy(birthDate = value) }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun save() {
        val state = _uiState.value

        val firstName = state.firstName.trim()
        val lastName = state.lastName.trim()

        if (firstName.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Ingresa el nombre")
            }
            return
        }

        if (lastName.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Ingresa los apellidos")
            }
            return
        }

        val parsedBirthDate = runCatching {
            LocalDate.parse(
                state.birthDate,
                DISPLAY_DATE_FORMATTER
            )
        }.getOrNull()

        if (parsedBirthDate == null) {
            _uiState.update {
                it.copy(
                    errorMessage = "Ingresa una fecha válida en formato dd/MM/yyyy"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            profileRepository.updateCareRecipient(
                careRecipientProfileId = state.careRecipientProfileId,
                firstName = firstName,
                lastName = lastName,
                birthDate = parsedBirthDate
            ).onSuccess { updated ->

                val currentContext = careContextStore.require()

                careContextStore.save(
                    currentContext.copy(
                        careRecipientName = updated.fullName
                    )
                )

                originalFirstName = updated.firstName
                originalLastName = updated.lastName
                originalBirthDate =
                    updated.birthDate.format(DISPLAY_DATE_FORMATTER)

                _uiState.update {
                    it.copy(
                        firstName = updated.firstName,
                        lastName = updated.lastName,
                        birthDate = updated.birthDate.format(
                            DISPLAY_DATE_FORMATTER
                        ),
                        isSaving = false,
                        isEditing = false,
                        successMessage = "Información actualizada correctamente"
                    )
                }

            }.onFailure {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "No se pudo actualizar la información"
                    )
                }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true)
            }

            runCatching {
                val recipient = profileRepository
                    .getCareRecipient(
                        careContext.careRecipientProfileId
                    )
                    .getOrThrow()

                val relationship = profileRepository
                    .getCareRelationships(
                        careContext.userAccountId
                    )
                    .getOrThrow()
                    .firstOrNull {
                        it.careRecipientProfileId ==
                                careContext.careRecipientProfileId
                    }

                CareRecipientUiState(
                    careRecipientProfileId = recipient.id,
                    firstName = recipient.firstName,
                    lastName = recipient.lastName,
                    birthDate = recipient.birthDate.format(
                        DISPLAY_DATE_FORMATTER
                    ),
                    relationshipLabel =
                        when (relationship?.relationshipType) {
                            RelationshipType.FAMILY -> "Familiar"
                            RelationshipType.CAREGIVER -> "Cuidador"
                            null -> ""
                        },
                    isRelationshipActive =
                        relationship?.active == true,
                    isLoading = false
                )
            }.onSuccess { loaded ->

                originalFirstName = loaded.firstName
                originalLastName = loaded.lastName
                originalBirthDate = loaded.birthDate

                _uiState.value = loaded

            }.onFailure {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "No se pudo cargar la información"
                    )
                }
            }
        }
    }

    private companion object {
        val DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}