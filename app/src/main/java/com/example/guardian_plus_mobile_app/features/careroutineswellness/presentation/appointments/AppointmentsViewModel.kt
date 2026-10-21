package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.appointments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.CancelReminderUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetRemindersUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.ROUTINE_RESULT_MESSAGE_KEY
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessage
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.todayBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppointmentsUiState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null,
    // Appointments still ahead (scheduled or already notified), soonest first
    val appointments: List<Reminder> = emptyList(),
    val now: Instant = Instant.EPOCH,
    val cancellingId: String? = null,
    val message: RoutineMessage? = null
) {
    val nextAppointment: Reminder? get() = appointments.firstOrNull()

    val laterAppointments: List<Reminder> get() = appointments.drop(1)
}

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val getReminders: GetRemindersUseCase,
    private val cancelReminder: CancelReminderUseCase,
    private val serverClock: ServerClock,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState: StateFlow<AppointmentsUiState> = _uiState.asStateFlow()

    init {
        load()
        observeFormResult()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            refresh()
        }
    }

    /** Cancels the appointment and its reminders; for a recurring one, the whole series. */
    fun cancelAppointment(appointment: Reminder) {
        if (_uiState.value.cancellingId != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(cancellingId = appointment.id) }
            cancelReminder(appointment.id)
                .onSuccess {
                    _uiState.update { it.copy(message = RoutineMessage.Resource(R.string.appointments_cancelled_message)) }
                    refresh()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(message = RoutineMessage.Text(e.message ?: "No se pudo cancelar la cita")) }
                }
            _uiState.update { it.copy(cancellingId = null) }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }

    private suspend fun refresh() {
        val now = serverClock.now()
        // From the start of today, so an appointment that is about to start still shows
        val (startOfDay, _) = todayBounds(now)
        getReminders(DemoSession.CARE_RECIPIENT_PROFILE_ID, from = startOfDay, type = ReminderType.APPOINTMENT)
            .onSuccess { reminders ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoaded = true,
                        errorMessage = null,
                        appointments = reminders.filter { reminder -> reminder.status.isPending }.sortedBy { reminder -> reminder.scheduledTime },
                        now = serverClock.now()
                    )
                }
            }
            .onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudieron cargar las citas") }
            }
    }

    private fun observeFormResult() {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<String?>(ROUTINE_RESULT_MESSAGE_KEY, null).filterNotNull().collect { text ->
                savedStateHandle[ROUTINE_RESULT_MESSAGE_KEY] = null
                _uiState.update { it.copy(message = RoutineMessage.Text(text)) }
                refresh()
            }
        }
    }
}
