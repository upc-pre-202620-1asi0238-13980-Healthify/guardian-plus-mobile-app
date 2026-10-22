package com.example.guardian_plus_mobile_app.core.session

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remembers the full name typed at sign-up until the onboarding creates the Profile with it. IAM keeps credentials
 * only, so without this the name would have to be typed twice.
 */
@Singleton
class SignUpNameStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun save(userAccountId: String, fullName: String) {
        preferences.edit().putString(userAccountId, fullName).apply()
    }

    fun find(userAccountId: String): String? = preferences.getString(userAccountId, null)

    fun remove(userAccountId: String) {
        preferences.edit().remove(userAccountId).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "guardian_sign_up_names"
    }
}
