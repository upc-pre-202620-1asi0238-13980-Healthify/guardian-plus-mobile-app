package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.FallConfirmationWindow
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import java.time.Duration
import java.time.Instant

/**
 * Seconds left before the alert escalates to the next contacts, or null when it will not escalate
 * (escalation off, already sent to everyone, or not waiting for an acknowledgement). Mirrors the
 * backend AckTimeoutEscalationScheduler, which counts from the last dispatch.
 */
fun Alert.escalationSecondsLeft(ackTimeoutSec: Int, escalationEnabled: Boolean, now: Instant): Long? {
    val dispatchedAt = lastDispatchedAt ?: return null
    val willEscalate = escalationEnabled &&
        severity.allowsEscalation &&
        status.isAwaitingAcknowledgement &&
        currentRecipientLevel != RecipientLevel.BROADCAST
    if (!willEscalate) return null
    val elapsed = Duration.between(dispatchedAt, now).seconds
    return (ackTimeoutSec - elapsed).coerceAtLeast(0)
}

/** Seconds the person under care still has to cancel a detected fall, or null outside that window. */
fun Alert.confirmationSecondsLeft(now: Instant): Long? {
    if (status != AlertStatus.PENDING_CONFIRMATION) return null
    val elapsed = Duration.between(triggeredAt, now).seconds
    return (FallConfirmationWindow.SECONDS - elapsed).coerceAtLeast(0)
}
