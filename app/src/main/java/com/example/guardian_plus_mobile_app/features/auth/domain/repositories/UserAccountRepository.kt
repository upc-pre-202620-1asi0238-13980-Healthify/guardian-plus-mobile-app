package com.example.guardian_plus_mobile_app.features.auth.domain.repositories

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email

interface UserAccountRepository {
    suspend fun getUserAccount(userAccountId: String): Result<UserAccount>

    suspend fun isEmailAvailable(email: Email): Result<Boolean>

    suspend fun setTwoFactor(userAccountId: String, enabled: Boolean): Result<UserAccount>
}
