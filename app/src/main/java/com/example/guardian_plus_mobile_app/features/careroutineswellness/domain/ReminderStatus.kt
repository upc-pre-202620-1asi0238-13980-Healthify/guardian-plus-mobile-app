package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

enum class ReminderStatus {
    SCHEDULED,
    ISSUED,
    CONFIRMED,
    CANCELLED,
    REISSUED,
    SUPPRESSED,
    MISSED;

    /** Still waiting for the person under care: not issued yet, or issued and not confirmed. */
    val isPending: Boolean get() = this == SCHEDULED || this == ISSUED || this == REISSUED

    /**
     * Whether it belongs to the person's day. Reminders cancelled by the family or suppressed during the
     * sleep window are not their responsibility, the same rule the platform applies to adherence.
     */
    val countsTowardsDay: Boolean get() = this != CANCELLED && this != SUPPRESSED
}
