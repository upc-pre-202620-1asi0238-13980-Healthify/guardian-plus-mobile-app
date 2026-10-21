package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.UserAccountStatus
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.AuthenticatedSessionDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.LoginResponseDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.UserAccountDto

fun UserAccountDto.toDomain() = UserAccount(
    id = id,
    email = Email(email),
    emailVerified = emailVerified,
    twoFactorEnabled = twoFactorEnabled,
    status = UserAccountStatus.valueOf(status)
)

fun AuthenticatedSessionDto.toDomain() = AuthenticatedSession(userAccountId = userAccountId, accessToken = token)

fun LoginResponseDto.toDomain(): LoginOutcome = when {
    !emailVerified -> LoginOutcome.EmailVerificationRequired(userAccountId)
    twoFactorRequired -> LoginOutcome.SecondFactorRequired(userAccountId)
    // The platform always sends the token once nothing else is missing
    else -> LoginOutcome.Authenticated(AuthenticatedSession(userAccountId, requireNotNull(token) { "Sesión sin token" }))
}
