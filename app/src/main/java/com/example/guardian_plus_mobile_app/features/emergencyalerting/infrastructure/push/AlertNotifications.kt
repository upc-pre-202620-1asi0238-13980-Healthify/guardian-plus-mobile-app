package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.push

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.guardian_plus_mobile_app.MainActivity
import com.example.guardian_plus_mobile_app.R

/**
 * The Android side of push alerts: the notification channels the platform names in every message, and the
 * notification shown when an alert arrives while the app is open (in the background Android shows it itself).
 */
object AlertNotifications {

    // Same ids the platform sends in android.notification.channel_id
    const val ALERTS_CHANNEL_ID = "guardian_alerts"
    const val SILENT_ALERTS_CHANNEL_ID = "guardian_alerts_silent"

    /** Data key FCM puts in the intent that opens the app from a notification; also used by ours. */
    const val ALERT_ID_EXTRA = "alertId"

    /** Silent mode turns off the sound of every alert except the critical ones. */
    fun channelFor(audible: Boolean): String = if (audible) ALERTS_CHANNEL_ID else SILENT_ALERTS_CHANNEL_ID

    /** Creates the channels once; Android keeps the user's changes to them afterwards. */
    fun registerChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                ALERTS_CHANNEL_ID,
                context.getString(R.string.notification_channel_alerts),
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = context.getString(R.string.notification_channel_alerts_description) }
        )
        manager.createNotificationChannel(
            NotificationChannel(
                SILENT_ALERTS_CHANNEL_ID,
                context.getString(R.string.notification_channel_silent_alerts),
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = context.getString(R.string.notification_channel_silent_alerts_description) }
        )
    }

    fun show(context: Context, alertId: String?, title: String?, body: String?, audible: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return
        val open = Intent(context, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra(ALERT_ID_EXTRA, alertId)
        val notification = NotificationCompat.Builder(context, channelFor(audible))
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(if (audible) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    alertId.hashCode(),
                    open,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()
        // Tagged with the alert, like the ones Android shows, so an update replaces the previous notification
        NotificationManagerCompat.from(context).notify(alertId, 0, notification)
    }
}
