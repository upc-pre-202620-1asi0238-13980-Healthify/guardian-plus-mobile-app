package com.example.guardian_plus_mobile_app.features.auth.domain

import com.example.guardian_plus_mobile_app.features.auth.domain.model.aggregates.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.Email
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.UserAccountStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserAccountTest {

    private fun account(emailVerified: Boolean, status: UserAccountStatus) =
        UserAccount("id", Email("maria@correo.com"), emailVerified, status)

    @Test
    fun `a pending account cannot authenticate`() {
        assertFalse(account(emailVerified = false, status = UserAccountStatus.PENDING_EMAIL_VERIFICATION).canAuthenticate())
    }

    @Test
    fun `a verified active account can authenticate`() {
        assertTrue(account(emailVerified = true, status = UserAccountStatus.ACTIVE).canAuthenticate())
    }
}
