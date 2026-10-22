package com.example.guardian_plus_mobile_app.features.profile.application

import com.example.guardian_plus_mobile_app.core.session.CareContext

/** What the app knows about the signed-in account once its Profile is read. */
sealed interface CareContextLoad {
    /** Profile and an active care relationship exist: the app can open Home. */
    data class Ready(val careContext: CareContext) : CareContextLoad

    /**
     * Something is missing, so the onboarding asks for it. [suggestedFullName] is the name typed at sign-up, if
     * this device remembers it.
     */
    data class OnboardingRequired(
        val userAccountId: String,
        val needsUserProfile: Boolean,
        val needsCareRecipient: Boolean,
        val suggestedFullName: String?
    ) : CareContextLoad
}
