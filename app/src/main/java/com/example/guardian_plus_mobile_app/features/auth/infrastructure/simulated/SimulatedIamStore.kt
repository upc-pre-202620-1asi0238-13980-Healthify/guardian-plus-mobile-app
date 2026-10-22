package com.example.guardian_plus_mobile_app.features.auth.infrastructure.simulated

import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.UserAccountStatus
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Accounts kept in memory while the platform has no IAM, shared by the simulated repositories. Every code
 * IAM would email (verification, OTP, password reset) is [SIMULATED_CODE], shown on the OTP screen as the demo code.
 */
@Singleton
class SimulatedIamStore @Inject constructor() {

    private val accounts = mutableMapOf<String, StoredAccount>()

    init {
        // The demo caregiver has two-factor on, so the OTP step can be shown
        accounts[DemoSession.CURRENT_USER_ID] = StoredAccount(
            account = UserAccount(
                id = DemoSession.CURRENT_USER_ID,
                email = Email(DEMO_EMAIL),
                emailVerified = true,
                twoFactorEnabled = true,
                status = UserAccountStatus.ACTIVE
            ),
            password = DEMO_PASSWORD
        )
    }

    fun findById(userAccountId: String): StoredAccount? = accounts[userAccountId]

    fun findByEmail(email: Email): StoredAccount? = accounts.values.firstOrNull { email.matches(it.account.email.value) }

    fun register(email: Email, password: String): UserAccount {
        val account = UserAccount(
            id = UUID.randomUUID().toString(),
            email = email,
            emailVerified = false,
            twoFactorEnabled = false,
            status = UserAccountStatus.PENDING_EMAIL_VERIFICATION
        )
        accounts[account.id] = StoredAccount(account, password)
        return account
    }

    fun update(account: UserAccount, password: String? = null) {
        val stored = accounts.getValue(account.id)
        accounts[account.id] = StoredAccount(account, password ?: stored.password)
    }

    data class StoredAccount(val account: UserAccount, val password: String)

    companion object {
        const val SIMULATED_CODE = DemoSession.VERIFICATION_CODE
        const val DEMO_EMAIL = "maria.rojas@guardianplus.pe"
        const val DEMO_PASSWORD = "Guardian123"
    }
}
