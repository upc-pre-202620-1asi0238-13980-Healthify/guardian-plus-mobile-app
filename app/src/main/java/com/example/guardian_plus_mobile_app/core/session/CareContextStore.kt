package com.example.guardian_plus_mobile_app.core.session

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Keeps the [CareContext] of the signed-in account on the device, so the screens have it right away, also after
 * the system restores the app in the middle of a screen. It is loaded after every sign-in and cleared on sign-out.
 */
@Singleton
class CareContextStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val _context = MutableStateFlow(read())

    /** The care context, or null before it is loaded (no session, or the onboarding is not finished). */
    val context: StateFlow<CareContext?> = _context.asStateFlow()

    fun current(): CareContext? = _context.value

    /**
     * The care context of the screens behind the sign-in. Home and every tab are only reachable once it is
     * loaded, so its absence is a navigation bug, not a user error.
     */
    fun require(): CareContext = checkNotNull(_context.value) { "The care context is not loaded" }

    fun save(careContext: CareContext) {
        preferences.edit()
            .putString(KEY_USER_ACCOUNT_ID, careContext.userAccountId)
            .putString(KEY_USER_FULL_NAME, careContext.userFullName)
            .putString(KEY_CARE_RECIPIENT_PROFILE_ID, careContext.careRecipientProfileId)
            .putString(KEY_CARE_RECIPIENT_NAME, careContext.careRecipientName)
            .putString(KEY_CALL_NUMBER, careContext.callNumber)
            .apply()
        _context.value = careContext
    }

    /** Updates the number the "call" buttons dial, e.g. after the emergency contacts change. */
    fun updateCallNumber(callNumber: String?) {
        val current = _context.value ?: return
        save(current.copy(callNumber = callNumber))
    }

    fun clear() {
        preferences.edit().clear().apply()
        _context.value = null
    }

    private fun read(): CareContext? {
        val userAccountId = preferences.getString(KEY_USER_ACCOUNT_ID, null) ?: return null
        return CareContext(
            userAccountId = userAccountId,
            userFullName = preferences.getString(KEY_USER_FULL_NAME, null) ?: return null,
            careRecipientProfileId = preferences.getString(KEY_CARE_RECIPIENT_PROFILE_ID, null) ?: return null,
            careRecipientName = preferences.getString(KEY_CARE_RECIPIENT_NAME, null) ?: return null,
            callNumber = preferences.getString(KEY_CALL_NUMBER, null)
        )
    }

    private companion object {
        const val PREFERENCES_NAME = "guardian_care_context"
        const val KEY_USER_ACCOUNT_ID = "user_account_id"
        const val KEY_USER_FULL_NAME = "user_full_name"
        const val KEY_CARE_RECIPIENT_PROFILE_ID = "care_recipient_profile_id"
        const val KEY_CARE_RECIPIENT_NAME = "care_recipient_name"
        const val KEY_CALL_NUMBER = "call_number"
    }
}
