package com.example.guardian_plus_mobile_app.features.profile.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
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
class ProfileHomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val careContextStore: CareContextStore
) : ViewModel() {

    private val careContext = careContextStore.require()

    private val _uiState = MutableStateFlow(ProfileHomeUiState())
    val uiState: StateFlow<ProfileHomeUiState> = _uiState.asStateFlow()

    init {
        loadRelationship()
    }

    private fun loadRelationship() {
        viewModelScope.launch {
            profileRepository.getCareRelationships(careContext.userAccountId)
                .onSuccess { relationships ->
                    val relationship = relationships.firstOrNull {
                        it.active &&
                                it.careRecipientProfileId == careContext.careRecipientProfileId
                    }

                    val label = when (relationship?.relationshipType) {
                        RelationshipType.FAMILY -> "Familiar"
                        RelationshipType.CAREGIVER -> "Cuidador"
                        null -> ""
                    }

                    _uiState.update {
                        it.copy(
                            relationshipLabel = label,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(isLoading = false)
                    }
                }
        }
    }
}