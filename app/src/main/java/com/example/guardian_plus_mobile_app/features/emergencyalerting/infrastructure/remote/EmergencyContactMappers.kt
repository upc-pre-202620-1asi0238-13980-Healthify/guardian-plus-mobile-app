package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact

fun EmergencyContactDto.toDomain(): EmergencyContact = EmergencyContact(
    id = id,
    careRecipientProfileId = careRecipientProfileId,
    userId = userId,
    displayName = displayName,
    relationship = relationship,
    phoneNumber = phoneNumber,
    priorityOrder = priorityOrder,
    active = active
)
