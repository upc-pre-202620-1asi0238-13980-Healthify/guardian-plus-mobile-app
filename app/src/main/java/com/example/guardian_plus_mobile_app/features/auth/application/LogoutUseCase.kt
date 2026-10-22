package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.SessionRepository
import javax.inject.Inject

/** Forgets the session and whom it cared for, so the next account starts clean. */
class LogoutUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val careContextStore: CareContextStore
) {
    operator fun invoke() {
        sessionRepository.clear()
        careContextStore.clear()
    }
}
