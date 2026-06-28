package com.yehia.prayertimes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yehia.prayertimes.utils.NotificationHelper

class BootReceiver : BroadcastReceiver() {
    private val TAG = "BootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Received broadcast action: $action. Rescheduling prayer alarms.")
        NotificationHelper.schedulePrayerAlarms(context)
    }
}
