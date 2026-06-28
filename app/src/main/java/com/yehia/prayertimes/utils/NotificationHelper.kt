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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

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

        if (prayerType == PrayerType.SUNRISE) return "OFF"
        if (prayerType == PrayerType.QIYAM) return "OFF"

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

    fun getPrePrayerOffset(context: Context, prayerType: PrayerType): Int {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getInt("pre_offset_${prayerType.name}", 0) // 0 = None
    }

    fun savePrePrayerOffset(context: Context, prayerType: PrayerType, offsetMinutes: Int) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putInt("pre_offset_${prayerType.name}", offsetMinutes).apply()
    }

    fun getIqamahOffset(context: Context, prayerType: PrayerType): Int {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getInt("iqamah_offset_${prayerType.name}", 0) // 0 = None
    }

    fun saveIqamahOffset(context: Context, prayerType: PrayerType, offsetMinutes: Int) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putInt("iqamah_offset_${prayerType.name}", offsetMinutes).apply()
    }

    fun getSavedMuezzin(context: Context): String {
        val sharedPref = context.getSharedPreferences("salam_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("athan_muezzin", "mishary") ?: "mishary"
    }

    fun downloadAthanFile(context: Context, muezzinKey: String) {
        val urlMap = mapOf(
            "mishary" to "https://www.islamcan.com/audio/adhan/azan2.mp3",
            "makkah" to "https://www.islamcan.com/audio/adhan/azan16.mp3",
            "madinah" to "https://www.islamcan.com/audio/adhan/azan20.mp3"
        )
        val urlStr = urlMap[muezzinKey] ?: return
        val destinationFile = File(context.filesDir, "athan_$muezzinKey.mp3")

        if (destinationFile.exists() && destinationFile.length() > 100000L) {
            Log.d(TAG, "Athan file for $muezzinKey already exists")
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting background download for muezzin $muezzinKey from $urlStr")
                val url = URL(urlStr)
                val connection = url.openConnection()
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.inputStream.use { input ->
                    FileOutputStream(destinationFile).use { output ->
                        input.copyTo(output)
                    }
                }
                Log.d(TAG, "Downloaded athan_$muezzinKey.mp3 successfully: ${destinationFile.length()} bytes")
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading Athan file for $muezzinKey", e)
                if (destinationFile.exists()) {
                    destinationFile.delete()
                }
            }
        }
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

        val targetPrayers = listOf(
            PrayerType.FAJR,
            PrayerType.SUNRISE,
            PrayerType.DHUHR,
            PrayerType.ASR,
            PrayerType.MAGHRIB,
            PrayerType.ISHA,
            PrayerType.QIYAM
        )

        // Trigger background download of chosen Athan file if needed
        downloadAthanFile(context, getSavedMuezzin(context))

        for (prayerType in targetPrayers) {
            val todayItem = todayResult.items.find { it.type == prayerType }
            val tomorrowItem = tomorrowResult.items.find { it.type == prayerType }

            if (todayItem == null || tomorrowItem == null) continue

            val mode = getPrayerNotificationMode(context, prayerType)

            // 1. STANDARD ADHAN ALARM
            val standardTargetTime = if (todayItem.time.after(now)) todayItem.time else tomorrowItem.time
            val standardTargetItem = if (todayItem.time.after(now)) todayItem else tomorrowItem

            if (mode != "OFF") {
                val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                    putExtra("PRAYER_NAME", standardTargetItem.name)
                    putExtra("PRAYER_TIME", standardTargetItem.formattedTime)
                    putExtra("PRAYER_TYPE", prayerType.name)
                    putExtra("ALERT_TYPE", "STANDARD")
                }

                val requestCode = 100 + prayerType.ordinal
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.cancel(pendingIntent)
                scheduleExactAlarmSafe(alarmManager, standardTargetTime.time, pendingIntent)
                Log.d(TAG, "Scheduled STANDARD alarm for ${standardTargetItem.name} at ${standardTargetTime}")
            } else {
                val intent = Intent(context, PrayerNotificationReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    100 + prayerType.ordinal,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                }
            }

            // 2. PRE-PRAYER ALARM
            val preOffset = getPrePrayerOffset(context, prayerType)
            if (preOffset > 0 && mode != "OFF") {
                val todayPreTime = todayItem.time.time - (preOffset * 60 * 1000L)
                val targetPreTime = if (todayPreTime > now.time) {
                    todayPreTime
                } else {
                    tomorrowItem.time.time - (preOffset * 60 * 1000L)
                }
                val targetItem = if (todayPreTime > now.time) todayItem else tomorrowItem

                val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                    putExtra("PRAYER_NAME", targetItem.name)
                    putExtra("PRAYER_TIME", targetItem.formattedTime)
                    putExtra("PRAYER_TYPE", prayerType.name)
                    putExtra("ALERT_TYPE", "PRE_PRAYER")
                    putExtra("OFFSET_MINUTES", preOffset)
                }

                val requestCode = 200 + prayerType.ordinal
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.cancel(pendingIntent)
                scheduleExactAlarmSafe(alarmManager, targetPreTime, pendingIntent)
                Log.d(TAG, "Scheduled PRE_PRAYER alarm for ${targetItem.name} at ${Date(targetPreTime)} ($preOffset mins early)")
            } else {
                val intent = Intent(context, PrayerNotificationReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    200 + prayerType.ordinal,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                }
            }

            // 3. IQAMAH ALARM
            val iqamahOffset = getIqamahOffset(context, prayerType)
            if (iqamahOffset > 0 && mode != "OFF") {
                val todayIqamahTime = todayItem.time.time + (iqamahOffset * 60 * 1000L)
                val targetIqamahTime = if (todayIqamahTime > now.time) {
                    todayIqamahTime
                } else {
                    tomorrowItem.time.time + (iqamahOffset * 60 * 1000L)
                }
                val targetItem = if (todayIqamahTime > now.time) todayItem else tomorrowItem

                val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                    putExtra("PRAYER_NAME", targetItem.name)
                    putExtra("PRAYER_TIME", targetItem.formattedTime)
                    putExtra("PRAYER_TYPE", prayerType.name)
                    putExtra("ALERT_TYPE", "IQAMAH")
                    putExtra("OFFSET_MINUTES", iqamahOffset)
                }

                val requestCode = 300 + prayerType.ordinal
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.cancel(pendingIntent)
                scheduleExactAlarmSafe(alarmManager, targetIqamahTime, pendingIntent)
                Log.d(TAG, "Scheduled IQAMAH alarm for ${targetItem.name} at ${Date(targetIqamahTime)} ($iqamahOffset mins after)")
            } else {
                val intent = Intent(context, PrayerNotificationReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    300 + prayerType.ordinal,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                }
            }
        }
        com.yehia.prayertimes.widget.PrayerWidgetProvider.triggerUpdate(context)
    }

    private fun scheduleExactAlarmSafe(alarmManager: AlarmManager, triggerTime: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            Log.e(TAG, "SecurityException while scheduling exact alarm: fallback used", e)
        }
    }

    fun cancelAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val targetPrayers = listOf(
            PrayerType.FAJR,
            PrayerType.SUNRISE,
            PrayerType.DHUHR,
            PrayerType.ASR,
            PrayerType.MAGHRIB,
            PrayerType.ISHA
        )

        for (prayerType in targetPrayers) {
            val intent = Intent(context, PrayerNotificationReceiver::class.java)
            // Cancel Standard (100)
            var pendingIntent = PendingIntent.getBroadcast(
                context,
                100 + prayerType.ordinal,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
            // Cancel Pre-Prayer (200)
            pendingIntent = PendingIntent.getBroadcast(
                context,
                200 + prayerType.ordinal,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
            // Cancel Iqamah (300)
            pendingIntent = PendingIntent.getBroadcast(
                context,
                300 + prayerType.ordinal,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}
