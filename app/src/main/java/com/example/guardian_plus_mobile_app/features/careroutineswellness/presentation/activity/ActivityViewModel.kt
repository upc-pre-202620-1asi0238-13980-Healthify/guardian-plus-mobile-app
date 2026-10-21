package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.ConfirmReminderUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetAdherenceUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetRemindersUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.ScheduleReminderUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Adherence
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ScheduleReminderRequest
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessage
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.lastWeek
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.todayBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActivityUiState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null,
    val todayReminders: List<Reminder> = emptyList(),
    val weeklyAdherence: Adherence? = null,
    val now: Instant = Instant.EPOCH,
    val busyReminderId: String? = null,
    // "+" swaps the plan for the "Nueva actividad" form, as in the prototype
    val isAdding: Boolean = false,
    val isSaving: Boolean = false,
    val message: RoutineMessage? = null
) {
    val blocks: List<Reminder>
        get() = todayReminders.filter { it.status.countsTowardsDay }.sortedBy { it.scheduledTime }

    val completedCount: Int get() = blocks.count { it.status == ReminderStatus.CONFIRMED }

    val percentage: Int get() = if (blocks.isEmpty()) 0 else completedCount * 100 / blocks.size

    val nextBlock: Reminder? get() = blocks.firstOrNull { it.status.isPending }
}

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val getReminders: GetRemindersUseCase,
    private val getAdherence: GetAdherenceUseCase,
    private val confirmReminder: ConfirmReminderUseCase,
    private val scheduleReminder: ScheduleReminderUseCase,
    private val serverClock: ServerClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    init {
        load()
        startAutoRefresh()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            refresh()
        }
    }

    fun toggleAdding() {
        _uiState.update { it.copy(isAdding = !it.isAdding) }
    }

    /** True when [time] is still ahead today; the plan only takes blocks of the rest of the day. */
    fun isLaterToday(time: LocalTime): Boolean {
        val now = serverClock.now()
        return now.atZone(ZoneId.systemDefault()).toLocalDate().atTime(time).atZone(ZoneId.systemDefault()).toInstant().isAfter(now)
    }

    /** "Añadir al plan": one block today at [time]. */
    fun addActivity(name: String, time: LocalTime, durationMinutes: Int) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val zone = ZoneId.systemDefault()
            val request = ScheduleReminderRequest(
                personUnderCareId = DemoSession.CARE_RECIPIENT_PROFILE_ID,
                type = ReminderType.PHYSICAL_ACTIVITY,
                scheduledTime = serverClock.now().atZone(zone).toLocalDate().atTime(time).atZone(zone).toInstant(),
                title = name,
                durationMinutes = durationMinutes
            )
            scheduleReminder(request)
                .onSuccess {
                    _uiState.update {
                        it.copy(isSaving = false, isAdding = false, message = RoutineMessage.Resource(R.string.activity_added_message))
                    }
                    refresh()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isSaving = false, message = RoutineMessage.Text(e.message ?: "No se pudo añadir la actividad"))
                    }
                }
        }
    }

    /** "Registrar como completada": the platform accepts it once the block was issued to the wristband. */
    fun completeBlock(reminder: Reminder) {
        if (_uiState.value.busyReminderId != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(busyReminderId = reminder.id) }
            confirmReminder(reminder.id)
                .onSuccess {
                    _uiState.update { it.copy(message = RoutineMessage.Resource(R.string.activity_completed_message)) }
                    refresh()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(message = RoutineMessage.Text(e.message ?: "No se pudo registrar la actividad")) }
                }
            _uiState.update { it.copy(busyReminderId = null) }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }

    private suspend fun refresh() = coroutineScope {
        val personUnderCareId = DemoSession.CARE_RECIPIENT_PROFILE_ID
        val now = serverClock.now()
        val (startOfDay, startOfTomorrow) = todayBounds(now)
        val (weekStart, weekEnd) = lastWeek(now)

        val adherence = async { getAdherence(personUnderCareId, ReminderType.PHYSICAL_ACTIVITY, weekStart, weekEnd) }
        getReminders(personUnderCareId, from = startOfDay, to = startOfTomorrow, type = ReminderType.PHYSICAL_ACTIVITY)
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
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar la actividad") }
            }
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_MS)
                val state = _uiState.value
                if (!state.isLoading && state.busyReminderId == null && !state.isSaving) refresh()
            }
        }
    }

    private companion object {
        const val AUTO_REFRESH_MS = 30_000L
    }
}
