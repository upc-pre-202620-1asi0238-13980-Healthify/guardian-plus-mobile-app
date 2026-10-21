package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.UserAccountRepository
import javax.inject.Inject

class GetUserAccountUseCase @Inject constructor(
    private val repository: UserAccountRepository
) {
    suspend operator fun invoke(userAccountId: String): Result<UserAccount> = repository.getUserAccount(userAccountId)
}
