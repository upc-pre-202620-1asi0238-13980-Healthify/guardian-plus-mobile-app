package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.UserAccountRepository
import javax.inject.Inject

/** The signed-in account (/user-accounts/me), resolved by the platform from the session token. */
class GetCurrentUserAccountUseCase @Inject constructor(
    private val repository: UserAccountRepository
) {
    suspend operator fun invoke(): Result<UserAccount> = repository.getCurrentUserAccount()
}
