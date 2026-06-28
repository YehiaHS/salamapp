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
import com.yehia.prayertimes.utils.HijriCalendar
import com.yehia.prayertimes.utils.NotificationHelper
import java.util.Calendar

/**
 * Small 2×2 Widget — shows next prayer name, time, and countdown.
 *
 * Request code range: 400–499 to avoid collisions with other widgets.
 */
class PrayerWidgetSmallProvider : AppWidgetProvider() {

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
            val views = RemoteViews(context.packageName, R.layout.prayer_widget_small)

            try {
                val latitude  = NotificationHelper.getSavedLatitude(context)
                val longitude = NotificationHelper.getSavedLongitude(context)
                val method    = NotificationHelper.getSavedMethod(context)
                val madhab    = NotificationHelper.getSavedMadhab(context)

                val result = PrayerRepository().calculatePrayerTimes(
                    latitude, longitude, method, madhab, Calendar.getInstance()
                )

                // Hijri date (short form: "15 Ramadan")
                val hijri    = HijriCalendar.getTodayHijri()
                val hijriStr = "${hijri.day} ${HijriCalendar.getHijriMonthNameEnglish(hijri.month)}"
                views.setTextViewText(R.id.widget_small_hijri, hijriStr)

                // Next prayer
                val next = result.nextPrayer
                if (next != null) {
                    views.setTextViewText(R.id.widget_small_prayer_name, next.name)
                    views.setTextViewText(R.id.widget_small_time, next.formattedTime)
                    views.setTextViewText(R.id.widget_small_remaining, formatRemaining(result.timeRemainingMs))
                } else {
                    views.setTextViewText(R.id.widget_small_prayer_name, "--")
                    views.setTextViewText(R.id.widget_small_time, "--:--")
                    views.setTextViewText(R.id.widget_small_remaining, "")
                }
            } catch (_: Exception) {
                views.setTextViewText(R.id.widget_small_prayer_name, "Salam")
                views.setTextViewText(R.id.widget_small_time, "--:--")
                views.setTextViewText(R.id.widget_small_remaining, "")
            }

            // Tap to open app
            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 400, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_small_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun formatRemaining(ms: Long): String {
            val totalMin = ms / 60000
            val hours    = totalMin / 60
            val minutes  = totalMin % 60
            return when {
                hours > 0   -> "${hours}h ${minutes}m"
                minutes > 0 -> "${minutes}m"
                else        -> "now"
            }
        }
    }
}
