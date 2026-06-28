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
import java.util.Calendar

/**
 * Medium 4×2 Widget — shows next prayer hero + all 5 prayer times in a compact row.
 *
 * Request code range: 500–599 to avoid collisions with other widgets.
 */
class PrayerWidgetMediumProvider : AppWidgetProvider() {

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
        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.prayer_widget_medium)

            try {
                val latitude  = NotificationHelper.getSavedLatitude(context)
                val longitude = NotificationHelper.getSavedLongitude(context)
                val method    = NotificationHelper.getSavedMethod(context)
                val madhab    = NotificationHelper.getSavedMadhab(context)

                val result = PrayerRepository().calculatePrayerTimes(
                    latitude, longitude, method, madhab, Calendar.getInstance()
                )

                // Header — Hijri date
                val hijri    = HijriCalendar.getTodayHijri()
                val hijriStr = "${hijri.day} ${HijriCalendar.getHijriMonthNameEnglish(hijri.month)} ${hijri.year}"
                views.setTextViewText(R.id.widget_medium_hijri, hijriStr)

                // Next prayer hero
                val next = result.nextPrayer
                if (next != null) {
                    views.setTextViewText(R.id.widget_medium_prayer_name, next.name)
                    views.setTextViewText(R.id.widget_medium_time, next.formattedTime)
                    views.setTextViewText(R.id.widget_medium_remaining, formatRemaining(result.timeRemainingMs))
                } else {
                    views.setTextViewText(R.id.widget_medium_prayer_name, "--")
                    views.setTextViewText(R.id.widget_medium_time, "--:--")
                    views.setTextViewText(R.id.widget_medium_remaining, "")
                }

                // All 5 prayers row (excluding Sunrise)
                val prayers = result.items.filter { it.type != PrayerType.SUNRISE }
                prayers.forEach { item ->
                    val (nameId, timeId) = when (item.type) {
                        PrayerType.FAJR    -> Pair(R.id.widget_medium_fajr_name,    R.id.widget_medium_fajr_time)
                        PrayerType.DHUHR   -> Pair(R.id.widget_medium_dhuhr_name,   R.id.widget_medium_dhuhr_time)
                        PrayerType.ASR     -> Pair(R.id.widget_medium_asr_name,     R.id.widget_medium_asr_time)
                        PrayerType.MAGHRIB -> Pair(R.id.widget_medium_maghrib_name, R.id.widget_medium_maghrib_time)
                        PrayerType.ISHA    -> Pair(R.id.widget_medium_isha_name,    R.id.widget_medium_isha_time)
                        else               -> return@forEach
                    }
                    // Shorten time to HH:mm only (drop AM/PM for compact row)
                    val shortTime = item.formattedTime.substringBefore(" ")
                    views.setTextViewText(nameId, item.name)
                    views.setTextViewText(timeId, shortTime)
                }

            } catch (_: Exception) {
                views.setTextViewText(R.id.widget_medium_prayer_name, "Salam")
                views.setTextViewText(R.id.widget_medium_time, "--:--")
                views.setTextViewText(R.id.widget_medium_remaining, "")
            }

            // Tap to open app
            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 500, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_medium_root, pendingIntent)

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
