package com.example.guardian_plus_mobile_app.features.auth.domain.repositories

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email

interface UserAccountRepository {
    /** The account of the current session, as the platform resolves it from the token. */
    suspend fun getCurrentUserAccount(): Result<UserAccount>

    suspend fun getUserAccount(userAccountId: String): Result<UserAccount>

    suspend fun isEmailAvailable(email: Email): Result<Boolean>
}
