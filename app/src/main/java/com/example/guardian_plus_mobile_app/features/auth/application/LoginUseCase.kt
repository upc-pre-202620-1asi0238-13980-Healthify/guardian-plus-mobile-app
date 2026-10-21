package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.LoginOutcome
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.SessionRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    // Without two-factor the session is issued right away, so it is kept here
    suspend operator fun invoke(email: Email, rawPassword: String): Result<LoginOutcome> =
        repository.login(email, rawPassword).onSuccess { outcome ->
            if (outcome is LoginOutcome.Authenticated) sessionRepository.save(outcome.session)
        }
}
