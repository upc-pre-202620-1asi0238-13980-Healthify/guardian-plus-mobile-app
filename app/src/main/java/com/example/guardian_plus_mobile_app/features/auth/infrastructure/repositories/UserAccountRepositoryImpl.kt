package com.example.guardian_plus_mobile_app.features.auth.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.UserAccountRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.dto.TwoFactorSettingRequestDto
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.services.UserAccountService
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.toDomain
import javax.inject.Inject

class UserAccountRepositoryImpl @Inject constructor(
    private val service: UserAccountService
) : UserAccountRepository {

    override suspend fun getUserAccount(userAccountId: String): Result<UserAccount> =
        apiCall({ service.getUserAccount(userAccountId) }) { it.toDomain() }

    override suspend fun isEmailAvailable(email: Email): Result<Boolean> =
        apiCall({ service.getEmailAvailability(email.value) }) { it.available }

    override suspend fun setTwoFactor(userAccountId: String, enabled: Boolean): Result<UserAccount> =
        apiCall({ service.setTwoFactor(userAccountId, TwoFactorSettingRequestDto(enabled)) }) { it.toDomain() }
}
