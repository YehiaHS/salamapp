package com.yehia.prayertimes.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Madhab
import com.yehia.prayertimes.data.PrayerRepository
import com.yehia.prayertimes.data.PrayerType
import com.yehia.prayertimes.receiver.PrayerNotificationReceiver
import java.util.Calendar
import java.util.Date

object NotificationHelper {

    private const val PREFS_NAME = "prayer_notification_prefs"
    const val CHANNEL_ID = "salam_prayer_notifications"
    private const val TAG = "NotificationHelper"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Salam Prayer Times"
            val descriptionText = "Notifications for Islamic prayer times (Fajr, Dhuhr, Asr, Maghrib, Isha)"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun savePrayerSettings(
        context: Context,
        latitude: Double,
        longitude: Double,
        method: CalculationMethod,
        madhab: Madhab
    ) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit()
            .putString("latitude", latitude.toString())
            .putString("longitude", longitude.toString())
            .putString("method", method.name)
            .putString("madhab", madhab.name)
            .apply()
    }

    fun getSavedLatitude(context: Context): Double {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString("latitude", LocationHelper.DEFAULT_LATITUDE.toString())?.toDoubleOrNull()
            ?: LocationHelper.DEFAULT_LATITUDE
    }

    fun getSavedLongitude(context: Context): Double {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString("longitude", LocationHelper.DEFAULT_LONGITUDE.toString())?.toDoubleOrNull()
            ?: LocationHelper.DEFAULT_LONGITUDE
    }

    fun getSavedMethod(context: Context): CalculationMethod {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val methodName = sharedPref.getString("method", CalculationMethod.EGYPTIAN.name)
        return try {
            CalculationMethod.valueOf(methodName ?: CalculationMethod.EGYPTIAN.name)
        } catch (e: Exception) {
            CalculationMethod.EGYPTIAN
        }
    }

    fun getSavedMadhab(context: Context): Madhab {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val madhabName = sharedPref.getString("madhab", Madhab.SHAFI.name)
        return try {
            Madhab.valueOf(madhabName ?: Madhab.SHAFI.name)
        } catch (e: Exception) {
            Madhab.SHAFI
        }
    }

    fun saveNotificationPreference(context: Context, prayerType: PrayerType, enabled: Boolean) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("notify_${prayerType.name}", enabled).apply()
    }

    fun getNotificationPreference(context: Context, prayerType: PrayerType): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean("notify_${prayerType.name}", true)
    }

    fun saveAthanPreference(context: Context, enabled: Boolean) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("play_athan", enabled).apply()
    }

    fun getAthanPreference(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean("play_athan", true)
    }

    fun getPrayerNotificationMode(context: Context, prayerType: PrayerType): String {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val mode = sharedPref.getString("mode_${prayerType.name}", null)
        if (mode != null) return mode

        // Backward compatibility fallback logic
        val isEnabled = getNotificationPreference(context, prayerType)
        if (!isEnabled) return "OFF"
        val playAthan = getAthanPreference(context)
        return if (playAthan) "ATHAN" else "BEEP"
    }

    fun savePrayerNotificationMode(context: Context, prayerType: PrayerType, mode: String) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putString("mode_${prayerType.name}", mode).apply()
    }

    fun schedulePrayerAlarms(context: Context) {
        val latitude = getSavedLatitude(context)
        val longitude = getSavedLongitude(context)
        val method = getSavedMethod(context)
        val madhab = getSavedMadhab(context)

        val repository = PrayerRepository()
        val now = Date()
        val calendarToday = Calendar.getInstance()
        val calendarTomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

        val todayResult = repository.calculatePrayerTimes(latitude, longitude, method, madhab, calendarToday)
        val tomorrowResult = repository.calculatePrayerTimes(latitude, longitude, method, madhab, calendarTomorrow)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // We only care about 5 prayers: Fajr, Dhuhr, Asr, Maghrib, Isha
        val targetPrayers = listOf(
            PrayerType.FAJR,
            PrayerType.DHUHR,
            PrayerType.ASR,
            PrayerType.MAGHRIB,
            PrayerType.ISHA
        )

        for (prayerType in targetPrayers) {
            // Find today's prayer time
            val todayItem = todayResult.items.find { it.type == prayerType }
            val tomorrowItem = tomorrowResult.items.find { it.type == prayerType }

            if (todayItem == null || tomorrowItem == null) continue

            // Determine if "today's" prayer has already passed
            val targetTime = if (todayItem.time.after(now)) {
                todayItem.time
            } else {
                tomorrowItem.time
            }

            val targetItem = if (todayItem.time.after(now)) todayItem else tomorrowItem

            // Unique Request Code for each prayer type (use its ordinal)
            val requestCode = prayerType.ordinal

            val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                putExtra("PRAYER_NAME", targetItem.name)
                putExtra("PRAYER_TIME", targetItem.formattedTime)
                putExtra("PRAYER_TYPE", prayerType.name)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Cancel any existing alarm of this type
            alarmManager.cancel(pendingIntent)

            // Schedule the alarm precisely
            val triggerTime = targetTime.time

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                        Log.d(TAG, "Scheduled EXACT alarm for ${targetItem.name} at ${targetItem.formattedTime}")
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                        Log.d(TAG, "Scheduled INEXACT alarm for ${targetItem.name} at ${targetItem.formattedTime} due to missing exact alarm permissions")
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    Log.d(TAG, "Scheduled EXACT alarm for ${targetItem.name} at ${targetItem.formattedTime}")
                }
            } catch (e: SecurityException) {
                // Graceful fallback
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                Log.e(TAG, "SecurityException while scheduling exact alarm: fallback used", e)
            }
        }
        // Update home screen widget
        com.yehia.prayertimes.widget.PrayerWidgetProvider.triggerUpdate(context)
    }


    fun cancelAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val targetPrayers = listOf(
            PrayerType.FAJR,
            PrayerType.DHUHR,
            PrayerType.ASR,
            PrayerType.MAGHRIB,
            PrayerType.ISHA
        )

        for (prayerType in targetPrayers) {
            val intent = Intent(context, PrayerNotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                prayerType.ordinal,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                Log.d(TAG, "Cancelled alarm for ${prayerType.name}")
            }
        }
    }
}
