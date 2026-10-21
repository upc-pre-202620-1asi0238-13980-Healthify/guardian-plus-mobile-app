package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.medicationform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.ScheduleReminderUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.RecurrenceFrequency
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ScheduleReminderRequest
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.firstOccurrence
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedicationFormUiState(
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    // Set once the platform scheduled the dose, so the screen can go back
    val savedTime: LocalTime? = null
)

@HiltViewModel
class MedicationFormViewModel @Inject constructor(
    private val scheduleReminder: ScheduleReminderUseCase,
    private val serverClock: ServerClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationFormUiState())
    val uiState: StateFlow<MedicationFormUiState> = _uiState.asStateFlow()

    /**
     * Schedules the dose; a confirmed one is discounted from the stock of the medication with the same name,
     * which the platform finds by itself.
     */
    fun save(
        name: String,
        dosage: String,
        frequency: RecurrenceFrequency,
        days: Set<DayOfWeek>,
        time: LocalTime,
        instructions: String?
    ) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val request = ScheduleReminderRequest(
                personUnderCareId = DemoSession.CARE_RECIPIENT_PROFILE_ID,
                type = ReminderType.MEDICATION,
                scheduledTime = firstOccurrence(time, frequency, days, serverClock.now()),
                title = name,
                dosage = dosage,
                instructions = instructions,
                frequency = frequency,
                daysOfWeek = if (frequency == RecurrenceFrequency.WEEKLY) days else emptySet()
            )
            scheduleReminder(request)
                .onSuccess { _uiState.update { it.copy(isSaving = false, savedTime = time) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "No se pudo programar la toma") }
                }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
