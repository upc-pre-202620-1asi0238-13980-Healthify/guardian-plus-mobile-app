package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.appointmentform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.ScheduleReminderUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ScheduleReminderRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppointmentFormUiState(
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saved: Boolean = false
)

@HiltViewModel
class AppointmentFormViewModel @Inject constructor(
    private val scheduleReminder: ScheduleReminderUseCase,
    private val serverClock: ServerClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentFormUiState())
    val uiState: StateFlow<AppointmentFormUiState> = _uiState.asStateFlow()

    /** Now on the platform's clock, so "the time already passed" is judged like the server will. */
    fun now(): Instant = serverClock.now()

    fun save(specialty: String, dateTime: LocalDateTime, location: String, leadTimeMinutes: Int) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val request = ScheduleReminderRequest(
                personUnderCareId = DemoSession.CARE_RECIPIENT_PROFILE_ID,
                type = ReminderType.APPOINTMENT,
                scheduledTime = dateTime.atZone(ZoneId.systemDefault()).toInstant(),
                title = specialty,
                location = location,
                leadTimeMinutes = leadTimeMinutes
            )
            scheduleReminder(request)
                .onSuccess { _uiState.update { it.copy(isSaving = false, saved = true) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "No se pudo agendar la cita") }
                }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
