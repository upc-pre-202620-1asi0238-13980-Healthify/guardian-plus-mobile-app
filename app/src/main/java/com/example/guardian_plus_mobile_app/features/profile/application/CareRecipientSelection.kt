package com.example.guardian_plus_mobile_app.features.profile.application

import com.example.guardian_plus_mobile_app.features.profile.domain.model.CareRelationship

/**
 * The active care relationship whose person the app shows. A member may care for several people from the same
 * account: the one just picked comes first, then the one the device already showed, and only then the first one.
 */
fun chooseCareRelationship(
    relationships: List<CareRelationship>,
    preferredCareRecipientProfileId: String?,
    currentCareRecipientProfileId: String?
): CareRelationship? {
    val active = relationships.filter { it.active }
    return active.firstOrNull { it.careRecipientProfileId == preferredCareRecipientProfileId }
        ?: active.firstOrNull { it.careRecipientProfileId == currentCareRecipientProfileId }
        ?: active.firstOrNull()
}
