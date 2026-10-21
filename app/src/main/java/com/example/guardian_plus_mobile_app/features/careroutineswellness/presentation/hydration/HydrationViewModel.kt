package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.hydration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.ConfigureHydrationPlanUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetHydrationPlanUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlan
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** What the settings show; before the first plan exists, the defaults the first change will create. */
data class HydrationSettings(
    val active: Boolean,
    val dailyGoalGlasses: Int,
    val intervalHours: Int,
    val respectSleepWindow: Boolean
)

data class HydrationUiState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null,
    // Null while the family has not configured a plan yet
    val plan: HydrationPlan? = null,
    val settings: HydrationSettings = DEFAULT_SETTINGS,
    val isSaving: Boolean = false,
    val message: RoutineMessage? = null
) {
    companion object {
        val DEFAULT_SETTINGS = HydrationSettings(active = false, dailyGoalGlasses = 6, intervalHours = 2, respectSleepWindow = true)
    }
}

@HiltViewModel
class HydrationViewModel @Inject constructor(
    private val getHydrationPlan: GetHydrationPlanUseCase,
    private val configureHydrationPlan: ConfigureHydrationPlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HydrationUiState())
    val uiState: StateFlow<HydrationUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getHydrationPlan(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .onSuccess { plan ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoaded = true,
                            plan = plan,
                            settings = plan?.toSettings() ?: HydrationUiState.DEFAULT_SETTINGS
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar la hidratación") }
                }
        }
    }

    fun setActive(active: Boolean) = save { it.copy(active = active) }

    fun setDailyGoal(glasses: Int) = save { it.copy(dailyGoalGlasses = glasses) }

    fun setInterval(hours: Int) = save { it.copy(intervalHours = hours) }

    fun setRespectSleepWindow(respect: Boolean) = save { it.copy(respectSleepWindow = respect) }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }

    /**
     * The platform replaces the whole plan on every change, so the four settings are always sent.
     * The control moves at once and goes back if the platform rejects the change.
     */
    private fun save(change: (HydrationSettings) -> HydrationSettings) {
        val state = _uiState.value
        if (state.isSaving) return
        val previous = state.settings
        val updated = change(previous)
        _uiState.update { it.copy(settings = updated, isSaving = true) }
        viewModelScope.launch {
            configureHydrationPlan(
                DemoSession.CARE_RECIPIENT_PROFILE_ID,
                updated.active,
                updated.dailyGoalGlasses,
                updated.intervalHours,
                updated.respectSleepWindow
            )
                .onSuccess { plan -> _uiState.update { it.copy(plan = plan, settings = plan.toSettings(), isSaving = false) } }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            settings = previous,
                            isSaving = false,
                            message = RoutineMessage.Text(e.message ?: "No se pudo guardar el plan de hidratación")
                        )
                    }
                }
        }
    }
}

private fun HydrationPlan.toSettings() = HydrationSettings(active, dailyGoalGlasses, intervalHours, respectSleepWindow)
