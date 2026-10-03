package com.example.guardian_plus_mobile_app.core.session

/**
 * Signed-in Care Circle member and the person under their care, simulated until the IAM and
 * Profile bounded contexts exist. The ids match emergency-alerting-demo.http, so the alerts
 * triggered there reach this app.
 */
object DemoSession {
    const val CURRENT_USER_ID = "1b3fdfe0-326f-4f36-80eb-b4d007cf4d19"
    const val CURRENT_USER_NAME = "María Rojas"
    const val CARE_RECIPIENT_PROFILE_ID = "6b0b1a3e-5f5e-4d6e-9a59-3c1c1f6c2b10"
    const val CARE_RECIPIENT_NAME = "Elena Rojas"
    const val CARE_RECIPIENT_FIRST_NAME = "Elena"
    const val CARE_RECIPIENT_PHONE = "+51987000111"
}
