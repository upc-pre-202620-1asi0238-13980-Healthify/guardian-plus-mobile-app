package com.example.guardian_plus_mobile_app.features.auth.infrastructure.simulated

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.UserAccountRepository
import javax.inject.Inject

/** Accounts served locally until the platform exposes /user-accounts. */
class SimulatedUserAccountRepository @Inject constructor(
    private val store: SimulatedIamStore
) : UserAccountRepository {

    override suspend fun getUserAccount(userAccountId: String): Result<UserAccount> =
        store.findById(userAccountId)?.let { Result.success(it.account) }
            ?: Result.failure(Exception("La cuenta no existe"))

    override suspend fun isEmailAvailable(email: Email): Result<Boolean> = Result.success(store.findByEmail(email) == null)

    override suspend fun setTwoFactor(userAccountId: String, enabled: Boolean): Result<UserAccount> {
        val stored = store.findById(userAccountId) ?: return Result.failure(Exception("La cuenta no existe"))
        val updated = stored.account.copy(twoFactorEnabled = enabled)
        store.update(updated)
        return Result.success(updated)
    }
}
