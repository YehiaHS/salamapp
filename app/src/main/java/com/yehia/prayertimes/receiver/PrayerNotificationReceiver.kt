package com.yehia.prayertimes.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.yehia.prayertimes.MainActivity
import com.yehia.prayertimes.data.PrayerType
import com.yehia.prayertimes.utils.NotificationHelper
import com.yehia.prayertimes.utils.LanguageManager

class PrayerNotificationReceiver : BroadcastReceiver() {

    private val TAG = "PrayerReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val rawPrayerName = intent.getStringExtra("PRAYER_NAME") ?: "Prayer"
        val prayerTime = intent.getStringExtra("PRAYER_TIME") ?: ""
        val prayerTypeName = intent.getStringExtra("PRAYER_TYPE") ?: ""

        // Initialize and load languages to translate title and text
        LanguageManager.loadLanguage(context)

        val prayerName = if (prayerTypeName.isNotEmpty()) {
            LanguageManager.get(prayerTypeName.lowercase()).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString()
            }
        } else {
            rawPrayerName
        }

        Log.d(TAG, "Alarm received for $prayerName ($prayerTime)")

        // 1. Resolve notification mode
        val prayerType = try {
            PrayerType.valueOf(prayerTypeName)
        } catch (e: Exception) {
            null
        }

        var notificationMode = "ATHAN"
        if (prayerType != null) {
            notificationMode = NotificationHelper.getPrayerNotificationMode(context, prayerType)
            if (notificationMode == "OFF") {
                Log.d(TAG, "Notification is disabled by user for $prayerName. Skipping notification post.")
                // Still reschedule the next alarms even if this specific one was disabled
                NotificationHelper.schedulePrayerAlarms(context)
                return
            }
        }

        // 2. Play Athan, Beep sound or keep silent if selected
        var customSoundUri: Uri? = null

        when (notificationMode) {
            "ATHAN" -> {
                val serviceIntent = Intent(context, AthanPlayerService::class.java).apply {
                    putExtra("PRAYER_NAME", prayerName)
                    putExtra("PRAYER_TIME", prayerTime)
                }
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                    Log.d(TAG, "AthanPlayerService started for Athan playback")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start AthanPlayerService, falling back to default beep", e)
                    customSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                }
            }
            "BEEP" -> {
                customSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                Log.d(TAG, "Setting standard beep notification alert sound")
            }
            "SILENT" -> {
                Log.d(TAG, "Notification mode is SILENT; sound triggers are skipped")
            }
        }

        // 3. Build and Display the Notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            100 + (prayerType?.ordinal ?: 0),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(com.yehia.prayertimes.R.drawable.ic_tasbih) // Proper single-color vector icon
            .setContentTitle("Salam - $prayerName Time")
            .setContentText("It is time for $prayerName prayer ($prayerTime).")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (customSoundUri != null) {
            notificationBuilder.setSound(customSoundUri)
        }

        notificationManager.notify(prayerType?.ordinal ?: 99, notificationBuilder.build())

        // 4. Reschedule future alarms automatically
        NotificationHelper.schedulePrayerAlarms(context)
    }
}
