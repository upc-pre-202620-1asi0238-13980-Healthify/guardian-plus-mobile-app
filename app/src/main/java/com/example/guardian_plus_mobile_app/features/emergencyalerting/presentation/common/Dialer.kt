package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

/** Opens the phone dialer with the number filled in. ACTION_DIAL needs no permission: the user presses call. */
fun Context.dial(phoneNumber: String) {
    startActivity(Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri()))
}
