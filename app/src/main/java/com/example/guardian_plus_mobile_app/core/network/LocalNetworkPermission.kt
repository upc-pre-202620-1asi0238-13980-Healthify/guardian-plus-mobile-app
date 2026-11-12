package com.example.guardian_plus_mobile_app.core.network

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.guardian_plus_mobile_app.BuildConfig
import java.net.InetAddress
import java.net.URI

/**
 * Asks for the local network permission when api.base.url points at a platform on this machine or the LAN.
 * From Android 17 INTERNET no longer reaches those addresses, and declaring the permission is not enough: it is a
 * runtime permission, and only an install with -g grants it on its own. The deployed platform does not need it.
 * [onResolved] runs once there is nothing left to ask, so no other permission request overlaps this one.
 */
@Composable
fun LocalNetworkPermission(onResolved: () -> Unit) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        onResolved()
    }
    LaunchedEffect(Unit) {
        val needed = Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN &&
            isLocalNetworkUrl(BuildConfig.API_BASE_URL) &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_LOCAL_NETWORK) !=
            PackageManager.PERMISSION_GRANTED
        if (needed) permissionLauncher.launch(Manifest.permission.ACCESS_LOCAL_NETWORK) else onResolved()
    }
}

// Only literal addresses are checked, so no DNS lookup happens: the emulator's 10.0.2.2, 127.0.0.1 or a LAN IP
private fun isLocalNetworkUrl(url: String): Boolean {
    val host = runCatching { URI(url).host }.getOrNull() ?: return false
    if (host == "localhost") return true
    if (!host.matches(Regex("""\d{1,3}(\.\d{1,3}){3}"""))) return false
    val address = runCatching { InetAddress.getByName(host) }.getOrNull() ?: return false
    return address.isLoopbackAddress || address.isSiteLocalAddress || address.isLinkLocalAddress
}
