package com.example.guardian_plus_mobile_app.features.auth.infrastructure.local

import android.content.Context
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.SessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Session kept in the app's private preferences, so a restart does not ask for the credentials again. */
@Singleton
class SharedPreferencesSessionRepository @Inject constructor(
    @ApplicationContext context: Context
) : SessionRepository {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun currentSession(): AuthenticatedSession? {
        val userAccountId = preferences.getString(KEY_USER_ACCOUNT_ID, null) ?: return null
        val accessToken = preferences.getString(KEY_ACCESS_TOKEN, null) ?: return null
        return AuthenticatedSession(userAccountId, accessToken)
    }

    override fun save(session: AuthenticatedSession) {
        preferences.edit()
            .putString(KEY_USER_ACCOUNT_ID, session.userAccountId)
            .putString(KEY_ACCESS_TOKEN, session.accessToken)
            .apply()
    }

    override fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "guardian_session"
        const val KEY_USER_ACCOUNT_ID = "user_account_id"
        const val KEY_ACCESS_TOKEN = "access_token"
    }
}
