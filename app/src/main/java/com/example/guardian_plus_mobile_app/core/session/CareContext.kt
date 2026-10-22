package com.example.guardian_plus_mobile_app.core.session

/**
 * Who is using the app and whom they care for, as the signed-in account's Profile says: the Care Circle member
 * (IAM's user account) and the person under their care, through their active care relationship.
 *
 * [callNumber] is the phone the "call" buttons dial: the care recipient's first emergency contact, since Profile
 * keeps no phone of the care recipient. It is null while there is no emergency contact.
 */
data class CareContext(
    val userAccountId: String,
    val userFullName: String,
    val careRecipientProfileId: String,
    val careRecipientName: String,
    val callNumber: String?
) {
    val userFirstName: String get() = userFullName.substringBefore(" ")

    val careRecipientFirstName: String get() = careRecipientName.substringBefore(" ")
}
