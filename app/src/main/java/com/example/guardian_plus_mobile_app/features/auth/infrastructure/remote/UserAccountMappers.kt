package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.UserAccountStatus
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.AuthenticatedSessionDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.OtpChallengeDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.UserAccountDto
import java.time.Instant

fun UserAccountDto.toDomain() = UserAccount(
    id = id,
    email = Email(email),
    emailVerified = emailVerified,
    status = UserAccountStatus.valueOf(status)
)

fun AuthenticatedSessionDto.toDomain() = AuthenticatedSession(
    userAccountId = userAccountId,
    accessToken = accessToken,
    expiresAt = Instant.parse(expiresAt)
)

fun OtpChallengeDto.toDomain(): LoginOutcome = LoginOutcome.SecondFactorRequired(userAccountId)
