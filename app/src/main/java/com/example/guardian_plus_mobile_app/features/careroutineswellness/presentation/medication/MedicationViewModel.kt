package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.medication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.ConfirmReminderUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetAdherenceUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetRemindersUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.MarkReminderAsMissedUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.ROUTINE_RESULT_MESSAGE_KEY
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessage
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.lastWeek
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.todayBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MedicationViewModel @Inject constructor(
    private val getReminders: GetRemindersUseCase,
    private val getAdherence: GetAdherenceUseCase,
    private val confirmReminder: ConfirmReminderUseCase,
    private val markReminderAsMissed: MarkReminderAsMissedUseCase,
    private val serverClock: ServerClock,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationUiState())
    val uiState: StateFlow<MedicationUiState> = _uiState.asStateFlow()

    init {
        load()
        observeFormResult()
        startAutoRefresh()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            refresh()
        }
    }

    /** "Confirmar toma": the platform only accepts it once the dose was issued to the wristband. */
    fun confirmDose(reminder: Reminder) = runOnDose(reminder) {
        confirmReminder(reminder.id).map { RoutineMessage.Resource(R.string.medication_confirmed_message) }
    }

    /** "Revisar": closes a dose the person never confirmed; nothing is discounted from the stock. */
    fun markDoseAsMissed(reminder: Reminder) = runOnDose(reminder) {
        markReminderAsMissed(reminder.id).map { RoutineMessage.Resource(R.string.medication_missed_message) }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }

    private fun runOnDose(reminder: Reminder, action: suspend () -> Result<RoutineMessage>) {
        if (_uiState.value.busyReminderId != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(busyReminderId = reminder.id) }
            action()
                .onSuccess { message ->
                    _uiState.update { it.copy(message = message) }
                    refresh()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(message = RoutineMessage.Text(e.message ?: "No se pudo actualizar la toma")) }
                }
            _uiState.update { it.copy(busyReminderId = null) }
        }
    }

    private suspend fun refresh() = coroutineScope {
        val personUnderCareId = DemoSession.CARE_RECIPIENT_PROFILE_ID
        val now = serverClock.now()
        val (startOfDay, startOfTomorrow) = todayBounds(now)
        val (weekStart, weekEnd) = lastWeek(now)

        val adherence = async { getAdherence(personUnderCareId, ReminderType.MEDICATION, weekStart, weekEnd) }
        getReminders(personUnderCareId, from = startOfDay, to = startOfTomorrow, type = ReminderType.MEDICATION)
            .onSuccess { reminders ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoaded = true,
                        errorMessage = null,
                        todayReminders = reminders,
                        weeklyAdherence = adherence.await().getOrNull(),
                        now = serverClock.now()
                    )
                }
            }
            .onFailure { e ->
                adherence.cancel()
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar la medicación") }
            }
    }

    // A dose scheduled on the form below comes back as a message to confirm it, and changes the day
    private fun observeFormResult() {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<String?>(ROUTINE_RESULT_MESSAGE_KEY, null).filterNotNull().collect { text ->
                savedStateHandle[ROUTINE_RESULT_MESSAGE_KEY] = null
                _uiState.update { it.copy(message = RoutineMessage.Text(text)) }
                refresh()
            }
        }
    }

    // Doses are issued and confirmed on the wristband, so the day is refreshed periodically
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_MS)
                if (!_uiState.value.isLoading && _uiState.value.busyReminderId == null) refresh()
            }
        }
    }

    private companion object {
        const val AUTO_REFRESH_MS = 30_000L
    }
}
