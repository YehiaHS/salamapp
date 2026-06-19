package com.yehia.prayertimes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yehia.prayertimes.utils.NotificationHelper

class BootReceiver : BroadcastReceiver() {
    private val TAG = "BootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device booted. Rescheduling prayer alarms.")
            NotificationHelper.schedulePrayerAlarms(context)
        }
    }
}
