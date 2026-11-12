package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.SessionRepository
import javax.inject.Inject

/** Exchanges the Google ID token for a platform session, which is kept like the one of the OTP sign-in. */
class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(idToken: String): Result<AuthenticatedSession> =
        repository.signInWithGoogle(idToken).onSuccess(sessionRepository::save)
}
