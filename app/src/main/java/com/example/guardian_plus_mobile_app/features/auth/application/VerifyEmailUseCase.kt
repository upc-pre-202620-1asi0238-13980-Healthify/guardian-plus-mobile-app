package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userAccountId: String, code: String): Result<UserAccount> =
        repository.verifyEmail(userAccountId, code)
}
