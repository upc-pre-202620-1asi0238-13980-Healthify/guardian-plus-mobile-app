package com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.profile.domain.model.CareRecipient
import com.example.guardian_plus_mobile_app.features.profile.domain.model.CareRelationship
import com.example.guardian_plus_mobile_app.features.profile.domain.model.RelationshipType
import com.example.guardian_plus_mobile_app.features.profile.domain.model.UserProfile
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CareRecipientProfileDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.CareRelationshipDto
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.dto.UserProfileDto
import java.time.LocalDate

fun UserProfileDto.toDomain() = UserProfile(
    id = id,
    userId = userId,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber
)

fun CareRecipientProfileDto.toDomain() = CareRecipient(
    id = id,
    firstName = firstName,
    lastName = lastName,
    birthDate = LocalDate.parse(birthDate)
)

fun CareRelationshipDto.toDomain() = CareRelationship(
    id = id,
    userId = userId,
    careRecipientProfileId = careRecipientProfileId,
    relationshipType = RelationshipType.valueOf(relationshipType),
    active = status == ACTIVE_STATUS
)

private const val ACTIVE_STATUS = "ACTIVE"
