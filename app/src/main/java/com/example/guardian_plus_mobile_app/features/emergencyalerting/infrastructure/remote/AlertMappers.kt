package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertDelivery
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertResponse
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettings
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.DeliveryStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.NotificationChannel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.ResponseStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import java.time.Instant

fun AlertSummaryDto.toDomain(): Alert = Alert(
    id = id,
    careRecipientProfileId = careRecipientProfileId,
    sourceType = AlertSourceType.valueOf(sourceType),
    severity = Severity.valueOf(severity),
    status = AlertStatus.valueOf(status),
    currentRecipientLevel = currentRecipientLevel?.let(RecipientLevel::valueOf),
    triggeredAt = Instant.parse(triggeredAt),
    acknowledgedAt = acknowledgedAt?.let(Instant::parse)
)

fun AlertDto.toDomain(): Alert = Alert(
    id = id,
    careRecipientProfileId = careRecipientProfileId,
    sourceType = AlertSourceType.valueOf(sourceType),
    severity = Severity.valueOf(severity),
    status = AlertStatus.valueOf(status),
    currentRecipientLevel = currentRecipientLevel?.let(RecipientLevel::valueOf),
    triggeredAt = Instant.parse(triggeredAt),
    acknowledgedAt = acknowledgedAt?.let(Instant::parse),
    confirmedAt = confirmedAt?.let(Instant::parse),
    lastDispatchedAt = lastDispatchedAt?.let(Instant::parse),
    acknowledgedByUserId = acknowledgedByUserId,
    resolvedAt = resolvedAt?.let(Instant::parse),
    deliveries = deliveries.orEmpty().map { it.toDomain() },
    responses = responses.orEmpty().map { it.toDomain() }
)

fun AlertDeliveryDto.toDomain(): AlertDelivery = AlertDelivery(
    id = id,
    recipientUserId = recipientUserId,
    recipientLevel = RecipientLevel.valueOf(recipientLevel),
    channel = NotificationChannel.valueOf(channel),
    status = DeliveryStatus.valueOf(deliveryStatus),
    sentAt = sentAt?.let(Instant::parse),
    deliveredAt = deliveredAt?.let(Instant::parse)
)

fun AlertResponseDto.toDomain(): AlertResponse = AlertResponse(
    id = id,
    responderUserId = responderUserId,
    status = ResponseStatus.valueOf(responseStatus),
    claimedAt = Instant.parse(claimedAt),
    completedAt = completedAt?.let(Instant::parse),
    notes = notes
)

fun AlertSettingsDto.toDomain(): AlertSettings = AlertSettings(
    careRecipientProfileId = careRecipientProfileId,
    primaryAckTimeoutSec = primaryAckTimeoutSec,
    escalationEnabled = escalationEnabled,
    silentModeEnabled = silentModeEnabled,
    broadcastCriticalImmediately = broadcastCriticalImmediately
)
