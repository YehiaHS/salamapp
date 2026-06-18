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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.yehia.prayertimes.MainActivity
import com.yehia.prayertimes.data.PrayerType
import com.yehia.prayertimes.utils.NotificationHelper

class PrayerNotificationReceiver : BroadcastReceiver() {

    private val TAG = "PrayerReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("PRAYER_NAME") ?: "Prayer"
        val prayerTime = intent.getStringExtra("PRAYER_TIME") ?: ""
        val prayerTypeName = intent.getStringExtra("PRAYER_TYPE") ?: ""

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
        var shouldPlayMediaPlayer = false

        when (notificationMode) {
            "ATHAN" -> {
                shouldPlayMediaPlayer = true
            }
            "BEEP" -> {
                customSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                Log.d(TAG, "Setting standard beep notification alert sound")
            }
            "SILENT" -> {
                Log.d(TAG, "Notification mode is SILENT; sound triggers are skipped")
            }
        }

        if (shouldPlayMediaPlayer) {
            // Check if user has embedded a raw 'athan' file
            val athanResId = context.resources.getIdentifier("athan", "raw", context.packageName)
            if (athanResId != 0) {
                try {
                    val mediaPlayer = MediaPlayer.create(context, athanResId)
                    mediaPlayer.setOnCompletionListener { mp -> mp.release() }
                    mediaPlayer.start()
                    Log.d(TAG, "Playing custom embedded athan sound")
                } catch (e: Exception) {
                    Log.e(TAG, "Error playing raw athan audio, falling back to default ringtone", e)
                    customSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                }
            } else {
                // If no custom raw resource, use default notification ringtone uri
                customSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                Log.d(TAG, "No raw 'athan' found; fell back to default notification sound")
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
            .setSmallIcon(context.applicationInfo.icon) // Use App launcher icon dynamically
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
