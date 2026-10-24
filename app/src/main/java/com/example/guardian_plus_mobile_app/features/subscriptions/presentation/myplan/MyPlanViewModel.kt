package com.example.guardian_plus_mobile_app.features.subscriptions.presentation.myplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.auth.application.GetCurrentSessionUseCase
import com.example.guardian_plus_mobile_app.features.subscriptions.domain.model.Subscription
import com.example.guardian_plus_mobile_app.features.subscriptions.domain.repositories.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MyPlanViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val getCurrentSessionUseCase: GetCurrentSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MyPlanUiState(
            isLoading = true
        )
    )

    val uiState: StateFlow<MyPlanUiState> =
        _uiState.asStateFlow()

    init {
        loadSubscription()
    }

    fun loadSubscription() {
        val session = getCurrentSessionUseCase()

        if (session == null) {
            _uiState.value = MyPlanUiState(
                isLoading = false,
                errorMessage = "No se encontró una sesión activa."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = MyPlanUiState(
                isLoading = true
            )

            val subscriptionResult =
                subscriptionRepository.findCurrentSubscription(
                    session.userAccountId
                )

            if (subscriptionResult.isFailure) {
                _uiState.value = MyPlanUiState(
                    isLoading = false,
                    errorMessage =
                        subscriptionResult.exceptionOrNull()?.message
                            ?: "No se pudo cargar la suscripción."
                )
                return@launch
            }

            val subscription =
                subscriptionResult.getOrNull()

            if (subscription == null) {
                _uiState.value = MyPlanUiState(
                    hasSubscription = false,
                    isLoading = false
                )
                return@launch
            }

            val entitlements =
                subscriptionRepository
                    .findEntitlements(
                        session.userAccountId
                    )
                    .getOrDefault(emptyList())

            _uiState.value = MyPlanUiState(
                subscriptionId = subscription.id,
                planName = "Guardian+",
                statusLabel =
                    subscription.status.toStatusLabel(),
                description =
                    "Protección y seguimiento para el círculo de cuidado",
                benefits =
                    entitlements.map { entitlement ->
                        entitlement.name.ifBlank {
                            entitlement.description
                        }
                    },
                billingCycle =
                    subscription.inferBillingCycle(),
                automaticRenewal =
                    subscription.status == "ACTIVE" &&
                            !subscription.cancelAtPeriodEnd,
                hasSubscription = true,
                isLoading = false
            )
        }
    }

    fun requestCancellation() {
        val subscriptionId =
            _uiState.value.subscriptionId ?: return

        if (_uiState.value.isCancelling) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isCancelling = true,
                    cancellationMessage = null,
                    cancellationError = null
                )

            subscriptionRepository
                .requestCancellation(subscriptionId)
                .onSuccess { subscription ->

                    _uiState.value =
                        _uiState.value.copy(
                            statusLabel =
                                subscription.status.toStatusLabel(),
                            automaticRenewal = false,
                            isCancelling = false,
                            cancellationMessage =
                                when (
                                    subscription.status
                                ) {
                                    "CANCELLATION_SCHEDULED" ->
                                        "La cancelación quedó programada para el final del periodo actual."

                                    "CANCELLED" ->
                                        "La suscripción fue cancelada correctamente."

                                    else ->
                                        "La suscripción fue actualizada correctamente."
                                }
                        )
                }
                .onFailure { error ->

                    _uiState.value =
                        _uiState.value.copy(
                            isCancelling = false,
                            cancellationError =
                                error.message
                                    ?: "No se pudo cancelar la suscripción."
                        )
                }
        }
    }

    private fun String.toStatusLabel(): String =
        when (this) {
            "ACTIVE" ->
                "Activo"

            "CANCELLATION_SCHEDULED" ->
                "Cancelación programada"

            "PENDING" ->
                "Pendiente"

            "CANCELLED" ->
                "Cancelado"

            "EXPIRED" ->
                "Expirado"

            else ->
                this
        }

    private fun Subscription.inferBillingCycle(): String {
        val start = currentPeriodStart
        val end = currentPeriodEnd

        if (
            start.isNullOrBlank() ||
            end.isNullOrBlank()
        ) {
            return "—"
        }

        return runCatching {

            val days =
                Duration.between(
                    Instant.parse(start),
                    Instant.parse(end)
                ).toDays()

            when {
                days in 27..32 ->
                    "Mensual"

                days in 360..370 ->
                    "Anual"

                else ->
                    "—"
            }

        }.getOrDefault("—")
    }
}