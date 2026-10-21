package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class ResendEmailVerificationUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userAccountId: String): Result<Unit> =
        repository.resendEmailVerification(userAccountId)
}
