package com.example.guardian_plus_mobile_app.features.profile.presentation.carecircle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.profile.domain.model.RelationshipType
import com.example.guardian_plus_mobile_app.features.profile.domain.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CareCircleViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val careContextStore: CareContextStore
) : ViewModel() {

    private val careContext = careContextStore.require()

    private val _uiState = MutableStateFlow(
        CareCircleUiState(
            careRecipientName = careContext.careRecipientName
        )
    )

    val uiState: StateFlow<CareCircleUiState> =
        _uiState.asStateFlow()

    init {
        load()
    }

    fun onSearchChange(value: String) {
        _uiState.update {
            it.copy(searchQuery = value)
        }
    }

    fun onFilterChange(filter: CareCircleFilter) {
        _uiState.update {
            it.copy(selectedFilter = filter)
        }
    }

    fun retry() {
        load()
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
                val relationships = profileRepository
                    .getCareRelationshipsByCareRecipient(
                        careContext.careRecipientProfileId
                    )
                    .getOrThrow()

                relationships
                    .map { relationship ->
                        async {
                            val profile = profileRepository
                                .findUserProfile(
                                    relationship.userId
                                )
                                .getOrThrow()

                            if (profile == null) {
                                null
                            } else {
                                CareCircleMemberUi(
                                    userId = relationship.userId,
                                    fullName =
                                        "${profile.firstName} ${profile.lastName}"
                                            .trim(),
                                    relationshipLabel =
                                        when (
                                            relationship.relationshipType
                                        ) {
                                            RelationshipType.FAMILY ->
                                                "Familiar"

                                            RelationshipType.CAREGIVER ->
                                                "Cuidador"
                                        },
                                    isActive =
                                        relationship.active
                                )
                            }
                        }
                    }
                    .awaitAll()
                    .filterNotNull()
                    .sortedBy { it.fullName }
            }.onSuccess { members ->
                _uiState.update {
                    it.copy(
                        members = members,
                        isLoading = false
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "No se pudo cargar el círculo de cuidado"
                    )
                }
            }
        }
    }
}