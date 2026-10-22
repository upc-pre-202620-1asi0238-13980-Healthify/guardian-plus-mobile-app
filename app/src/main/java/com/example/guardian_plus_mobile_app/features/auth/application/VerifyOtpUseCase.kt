package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.SessionRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(userAccountId: String, code: String): Result<AuthenticatedSession> =
        repository.verifyOtp(userAccountId, code).onSuccess(sessionRepository::save)
}
