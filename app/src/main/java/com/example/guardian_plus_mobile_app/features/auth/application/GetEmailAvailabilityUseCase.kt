package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.UserAccountRepository
import javax.inject.Inject

class GetEmailAvailabilityUseCase @Inject constructor(
    private val repository: UserAccountRepository
) {
    suspend operator fun invoke(email: Email): Result<Boolean> = repository.isEmailAvailable(email)
}
