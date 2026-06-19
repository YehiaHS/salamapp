package com.yehia.prayertimes.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.yehia.prayertimes.utils.NotificationHelper

class AthanPlayerService : Service() {

    private val TAG = "AthanPlayerService"
    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Athan service created")
        
        // Acquire wake lock to keep CPU awake during playback
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SalamApp::AthanPlayback").apply {
            setReferenceCounted(false)
            acquire(10 * 60 * 1000L) // Limit to 10 minutes max safety timeout
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prayerName = intent?.getStringExtra("PRAYER_NAME") ?: "Prayer"
        val prayerTime = intent?.getStringExtra("PRAYER_TIME") ?: ""
        
        Log.d(TAG, "Starting Athan playback for $prayerName at $prayerTime")

        // Start as Foreground Service
        startAthanForeground(prayerName, prayerTime)

        // Initialize and play MediaPlayer
        playAthan()

        return START_NOT_STICKY
    }

    private fun startAthanForeground(prayerName: String, prayerTime: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Ensure channel exists
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationHelper.CHANNEL_ID,
                "Salam Prayer Times",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(applicationInfo.icon)
            .setContentTitle("Athan - $prayerName Time")
            .setContentText("It is time for $prayerName prayer ($prayerTime).")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1001,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(1001, notification)
        }
    }

    private fun playAthan() {
        val athanResId = resources.getIdentifier("athan", "raw", packageName)
        if (athanResId != 0) {
            try {
                mediaPlayer = MediaPlayer.create(this, athanResId).apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setOnCompletionListener {
                        Log.d(TAG, "Athan playback completed")
                        stopSelf()
                    }
                    start()
                }
                Log.d(TAG, "Athan sound started successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error playing raw athan audio", e)
                stopSelf()
            }
        } else {
            Log.d(TAG, "No raw 'athan' resource found, stopping service")
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
        
        Log.d(TAG, "Athan service destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
