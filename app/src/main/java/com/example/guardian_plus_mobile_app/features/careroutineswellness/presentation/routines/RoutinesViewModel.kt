package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetActivityMonitorUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetHydrationPlanUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetMedicationStocksUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetRemindersUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.application.GetSleepCycleRecordsUseCase
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val getReminders: GetRemindersUseCase,
    private val getHydrationPlan: GetHydrationPlanUseCase,
    private val getSleepCycleRecords: GetSleepCycleRecordsUseCase,
    private val getActivityMonitor: GetActivityMonitorUseCase,
    private val getMedicationStocks: GetMedicationStocksUseCase,
    private val serverClock: ServerClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutinesUiState(careRecipientName = DemoSession.CARE_RECIPIENT_NAME))
    val uiState: StateFlow<RoutinesUiState> = _uiState.asStateFlow()

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

    // The sections are independent, so they are requested together instead of one after another
    private suspend fun refresh() = coroutineScope {
        val personUnderCareId = DemoSession.CARE_RECIPIENT_PROFILE_ID
        val now = serverClock.now()
        val zone = ZoneId.systemDefault()
        val today = now.atZone(zone).toLocalDate()
        val startOfDay = today.atStartOfDay(zone).toInstant()
        val startOfTomorrow = today.plusDays(1).atStartOfDay(zone).toInstant()

        val todayReminders = async { getReminders(personUnderCareId, from = startOfDay, to = startOfTomorrow) }
        val appointments = async { getReminders(personUnderCareId, from = now, type = ReminderType.APPOINTMENT) }
        val hydrationPlan = async { getHydrationPlan(personUnderCareId) }
        val sleepRecords = async { getSleepCycleRecords(personUnderCareId) }
        val activityMonitor = async { getActivityMonitor(personUnderCareId) }
        val medicationStocks = async { getMedicationStocks(personUnderCareId) }

        // Without today's reminders there is no day to show; the other sections just stay empty
        todayReminders.await()
            .onSuccess { reminders ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoaded = true,
                        errorMessage = null,
                        todayReminders = reminders,
                        nextAppointment = appointments.await().getOrNull()
                            ?.firstOrNull { appointment -> appointment.status.isPending },
                        hydrationPlan = hydrationPlan.await().getOrNull(),
                        lastSleep = sleepRecords.await().getOrNull()?.firstOrNull(),
                        activityMonitor = activityMonitor.await().getOrNull(),
                        medicationStocks = medicationStocks.await().getOrDefault(emptyList()),
                        now = serverClock.now()
                    )
                }
            }
            .onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "No se pudieron cargar las rutinas")
                }
            }
    }

    // Reminders are issued and confirmed on the wristband, so the day is refreshed periodically
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_MS)
                if (!_uiState.value.isLoading) refresh()
            }
        }
    }

    private companion object {
        const val AUTO_REFRESH_MS = 30_000L
    }
}
