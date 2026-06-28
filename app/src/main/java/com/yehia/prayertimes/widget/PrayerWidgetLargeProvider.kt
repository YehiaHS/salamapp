package com.yehia.prayertimes.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.yehia.prayertimes.MainActivity
import com.yehia.prayertimes.R
import com.yehia.prayertimes.data.PrayerRepository
import com.yehia.prayertimes.data.PrayerType
import com.yehia.prayertimes.utils.HijriCalendar
import com.yehia.prayertimes.utils.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Large 4×4 Widget — full prayer schedule, next prayer hero, Hijri date, update time.
 *
 * Request code range: 600–699 to avoid collisions with other widgets.
 */
class PrayerWidgetLargeProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        private val updateTimeFmt = SimpleDateFormat("h:mm a", Locale.getDefault())

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.prayer_widget_large)

            try {
                val latitude  = NotificationHelper.getSavedLatitude(context)
                val longitude = NotificationHelper.getSavedLongitude(context)
                val method    = NotificationHelper.getSavedMethod(context)
                val madhab    = NotificationHelper.getSavedMadhab(context)

                val result = PrayerRepository().calculatePrayerTimes(
                    latitude, longitude, method, madhab, Calendar.getInstance()
                )

                // Hijri date (full: "15 Dhul-Hijjah 1447")
                val hijri    = HijriCalendar.getTodayHijri()
                val hijriStr = "${hijri.day} ${HijriCalendar.getHijriMonthNameEnglish(hijri.month)} ${hijri.year}"
                views.setTextViewText(R.id.widget_large_hijri, hijriStr)

                // Next prayer hero
                val next = result.nextPrayer
                if (next != null) {
                    views.setTextViewText(R.id.widget_large_next_name, next.name)
                    views.setTextViewText(R.id.widget_large_countdown, formatRemaining(result.timeRemainingMs))
                } else {
                    views.setTextViewText(R.id.widget_large_next_name, "--")
                    views.setTextViewText(R.id.widget_large_countdown, "")
                }

                // All 6 prayer rows
                result.items.filter { it.type != PrayerType.QIYAM }.forEach { item ->
                    val (nameId, timeId) = when (item.type) {
                        PrayerType.FAJR    -> Pair(R.id.widget_large_fajr_name,    R.id.widget_large_fajr_time)
                        PrayerType.SUNRISE -> Pair(R.id.widget_large_sunrise_name, R.id.widget_large_sunrise_time)
                        PrayerType.DHUHR   -> Pair(R.id.widget_large_dhuhr_name,   R.id.widget_large_dhuhr_time)
                        PrayerType.ASR     -> Pair(R.id.widget_large_asr_name,     R.id.widget_large_asr_time)
                        PrayerType.MAGHRIB -> Pair(R.id.widget_large_maghrib_name, R.id.widget_large_maghrib_time)
                        PrayerType.ISHA    -> Pair(R.id.widget_large_isha_name,    R.id.widget_large_isha_time)
                        else               -> return@forEach
                    }
                    views.setTextViewText(nameId, item.name)
                    views.setTextViewText(timeId, item.formattedTime)
                }

                // Update time footer
                views.setTextViewText(
                    R.id.widget_large_updated,
                    "Updated ${updateTimeFmt.format(Calendar.getInstance().time)}"
                )

            } catch (_: Exception) {
                views.setTextViewText(R.id.widget_large_next_name, "Salam")
                views.setTextViewText(R.id.widget_large_countdown, "")
            }

            // Tap anywhere to open app
            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 600, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_large_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun formatRemaining(ms: Long): String {
            val totalMin = ms / 60000
            val hours    = totalMin / 60
            val minutes  = totalMin % 60
            return when {
                hours > 0   -> "in ${hours}h ${minutes}m"
                minutes > 0 -> "in ${minutes}m"
                else        -> "now"
            }
        }
    }
}
