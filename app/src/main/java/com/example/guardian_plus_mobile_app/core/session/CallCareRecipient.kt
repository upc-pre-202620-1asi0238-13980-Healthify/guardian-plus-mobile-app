package com.example.guardian_plus_mobile_app.core.session

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.example.guardian_plus_mobile_app.R

/**
 * What every "call" button does: opens the dialer with the care recipient's first emergency contact
 * ([CareContext.callNumber]). Without one, it says how to get a number to call instead of doing nothing.
 */
fun Context.callCareRecipient(careContext: CareContext) {
    val number = careContext.callNumber
    if (number == null) {
        Toast.makeText(this, R.string.call_no_emergency_contact, Toast.LENGTH_LONG).show()
        return
    }
    // ACTION_DIAL needs no permission: the user presses call
    startActivity(Intent(Intent.ACTION_DIAL, "tel:$number".toUri()))
}
