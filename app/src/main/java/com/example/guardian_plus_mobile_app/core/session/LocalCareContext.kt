package com.example.guardian_plus_mobile_app.core.session

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The [CareContext] of the signed-in account for the composables behind the sign-in, provided by the app shell
 * (AppNavHost). The default only serves the @Preview functions, which have no shell around them.
 */
val LocalCareContext = staticCompositionLocalOf {
    CareContext(
        userAccountId = "preview-user",
        userFullName = "María Rojas",
        careRecipientProfileId = "preview-care-recipient",
        careRecipientName = "Elena Rojas",
        callNumber = "+51987000111"
    )
}
