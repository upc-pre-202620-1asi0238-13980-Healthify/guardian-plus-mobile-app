package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class RegisterUserCredentialsUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: Email, rawPassword: String): Result<UserAccount> =
        repository.register(email, rawPassword)
}
