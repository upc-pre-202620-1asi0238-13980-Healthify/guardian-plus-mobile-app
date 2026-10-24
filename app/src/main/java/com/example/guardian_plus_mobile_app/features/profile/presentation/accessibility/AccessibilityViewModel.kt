package com.example.guardian_plus_mobile_app.features.profile.presentation.accessibility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.profile.domain.model.AppFontScale
import com.example.guardian_plus_mobile_app.features.profile.domain.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AccessibilityViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    careContextStore: CareContextStore
) : ViewModel() {

    private val userId =
        careContextStore.require().userAccountId

    private val _uiState =
        MutableStateFlow(AccessibilityUiState())

    val uiState: StateFlow<AccessibilityUiState> =
        _uiState.asStateFlow()

    init {
        load()
    }

    fun selectFontScale(
        fontScale: AppFontScale
    ) {
        _uiState.update {
            it.copy(fontScale = fontScale)
        }
    }

    fun setHighContrast(enabled: Boolean) {
        _uiState.update {
            it.copy(highContrastEnabled = enabled)
        }
    }

    fun setReduceMotion(enabled: Boolean) {
        _uiState.update {
            it.copy(reduceMotionEnabled = enabled)
        }
    }

    fun resetDefaults() {
        _uiState.update {
            it.copy(
                fontScale = AppFontScale.DEFAULT,
                highContrastEnabled = false,
                reduceMotionEnabled = false
            )
        }
    }

    fun save() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSaving = true)
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
                                "Preferencias actualizadas correctamente"
                        )
                    }
                }
                .onFailure {
                    _uiState.update { current ->
                        current.copy(
                            isSaving = false,
                            errorMessage =
                                "No se pudieron actualizar las preferencias"
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
            profileRepository
                .findUserPreferences(userId)
                .onSuccess { preferences ->
                    if (preferences == null) {
                        _uiState.update {
                            it.copy(isLoading = false)
                        }
                    } else {
                        _uiState.value =
                            AccessibilityUiState(
                                language =
                                    preferences.language,
                                fontScale =
                                    preferences.fontScale,
                                highContrastEnabled =
                                    preferences
                                        .highContrastEnabled,
                                reduceMotionEnabled =
                                    preferences
                                        .reduceMotionEnabled,
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