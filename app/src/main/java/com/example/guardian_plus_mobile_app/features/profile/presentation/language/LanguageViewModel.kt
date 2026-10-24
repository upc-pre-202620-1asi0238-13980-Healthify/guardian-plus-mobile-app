package com.example.guardian_plus_mobile_app.features.profile.presentation.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.profile.domain.model.AppLanguage
import com.example.guardian_plus_mobile_app.features.profile.domain.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    careContextStore: CareContextStore
) : ViewModel() {

    private val userId =
        careContextStore.require().userAccountId

    private val _uiState =
        MutableStateFlow(LanguageUiState())

    val uiState: StateFlow<LanguageUiState> =
        _uiState.asStateFlow()

    init {
        load()
    }

    fun selectLanguage(
        language: AppLanguage
    ) {
        _uiState.update {
            it.copy(
                language = language,
                successMessage = null,
                errorMessage = null
            )
        }
    }

    fun save() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            profileRepository
                .updateLanguageAndAccessibilityPreferences(
                    userId = userId,
                    language = state.language,
                    highContrastEnabled =
                        state.highContrastEnabled,
                    reduceMotionEnabled =
                        state.reduceMotionEnabled,
                    fontScale = state.fontScale
                )
                .onSuccess {
                    _uiState.update { current ->
                        current.copy(
                            isSaving = false,
                            successMessage =
                                "Idioma actualizado correctamente"
                        )
                    }
                }
                .onFailure {
                    _uiState.update { current ->
                        current.copy(
                            isSaving = false,
                            errorMessage =
                                "No se pudo actualizar el idioma"
                        )
                    }
                }
        }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(
                successMessage = null,
                errorMessage = null
            )
        }
    }

    private fun load() {
        viewModelScope.launch {
            profileRepository
                .findUserPreferences(userId)
                .onSuccess { preferences ->
                    if (preferences == null) {
                        _uiState.update {
                            it.copy(isLoading = false)
                        }
                    } else {
                        _uiState.value =
                            LanguageUiState(
                                language =
                                    preferences.language,
                                highContrastEnabled =
                                    preferences
                                        .highContrastEnabled,
                                reduceMotionEnabled =
                                    preferences
                                        .reduceMotionEnabled,
                                fontScale =
                                    preferences.fontScale,
                                isLoading = false
                            )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                "No se pudieron cargar las preferencias"
                        )
                    }
                }
        }
    }
}