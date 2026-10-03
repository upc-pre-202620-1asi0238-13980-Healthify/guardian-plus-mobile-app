package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Incident
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentStatus
import java.time.Instant

fun IncidentDto.toDomain(): Incident = Incident(
    id = id,
    alertId = alertId,
    status = IncidentStatus.valueOf(status),
    markedInAttentionAt = Instant.parse(markedInAttentionAt),
    stabilizedAt = stabilizedAt?.let(Instant::parse),
    closedAt = closedAt?.let(Instant::parse),
    notes = notes
)
