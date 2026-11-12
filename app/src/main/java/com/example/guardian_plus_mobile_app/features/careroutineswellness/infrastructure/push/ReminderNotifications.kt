package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.push

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
 * The Android side of the preventive reminders the platform pushes to the caregivers (e.g. a medical appointment
 * coming up): their own notification channel, kept apart from the alerts, and the notification shown when one
 * arrives while the app is open (in the background Android shows it itself).
 */
object ReminderNotifications {

    // Same id the platform sends in android.notification.channel_id
    const val REMINDERS_CHANNEL_ID = "guardian_reminders"

    /** Data key FCM puts in the intent that opens the app from a notification; also used by ours. */
    const val KIND_EXTRA = "kind"

    /** Value of [KIND_EXTRA] that marks the reminder of a medical appointment. */
    const val APPOINTMENT_REMINDER_KIND = "APPOINTMENT_REMINDER"

    private const val REMINDER_ID_EXTRA = "reminderId"

    fun isAppointmentReminder(data: Map<String, String>): Boolean = data[KIND_EXTRA] == APPOINTMENT_REMINDER_KIND

    fun isAppointmentReminder(intent: Intent?): Boolean = intent?.getStringExtra(KIND_EXTRA) == APPOINTMENT_REMINDER_KIND

    /** Creates the channel once; Android keeps the user's changes to it afterwards. */
    fun registerChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(
                REMINDERS_CHANNEL_ID,
                context.getString(R.string.notification_channel_reminders),
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = context.getString(R.string.notification_channel_reminders_description) }
        )
    }

    fun showAppointmentReminder(context: Context, data: Map<String, String>, title: String?, body: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return
        val reminderId = data[REMINDER_ID_EXTRA]
        // Same extras as the notification Android shows in the background, so both open the appointments
        val open = Intent(context, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra(KIND_EXTRA, APPOINTMENT_REMINDER_KIND)
            .putExtra(REMINDER_ID_EXTRA, reminderId)
        val notification = NotificationCompat.Builder(context, REMINDERS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    reminderId.hashCode(),
                    open,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()
        // Tagged with the reminder, like the ones Android shows, so a repeated push replaces the previous one
        NotificationManagerCompat.from(context).notify(reminderId, 0, notification)
    }
}
