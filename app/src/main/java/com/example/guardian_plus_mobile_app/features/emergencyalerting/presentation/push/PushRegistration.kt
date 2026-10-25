package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.push

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * Registers this phone for push alerts once [userAccountId] is signed in, asking for the notification
 * permission first on Android 13+. Without it the phone is not registered: alerts still show inside the app.
 */
@Composable
fun PushRegistration(
    userAccountId: String?,
    viewModel: PushRegistrationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && userAccountId != null) viewModel.onSignedIn(userAccountId)
    }
    LaunchedEffect(userAccountId) {
        when {
            userAccountId == null -> viewModel.onSignedOut()
            NotificationManagerCompat.from(context).areNotificationsEnabled() -> viewModel.onSignedIn(userAccountId)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
