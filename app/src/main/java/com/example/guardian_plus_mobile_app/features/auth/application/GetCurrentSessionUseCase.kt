package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.SessionRepository
import javax.inject.Inject

class GetCurrentSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): AuthenticatedSession? = sessionRepository.currentSession()
}
