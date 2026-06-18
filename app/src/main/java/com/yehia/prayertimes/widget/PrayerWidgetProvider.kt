package com.yehia.prayertimes.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.yehia.prayertimes.MainActivity
import com.yehia.prayertimes.R
import com.yehia.prayertimes.data.PrayerRepository
import com.yehia.prayertimes.utils.HijriCalendar
import com.yehia.prayertimes.utils.NotificationHelper
import java.util.Calendar

class PrayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.prayer_widget)

        try {
            // 1. Fetch saved location and preferences
            val latitude = NotificationHelper.getSavedLatitude(context)
            val longitude = NotificationHelper.getSavedLongitude(context)
            val method = NotificationHelper.getSavedMethod(context)
            val madhab = NotificationHelper.getSavedMadhab(context)

            // 2. Calculate prayer times for today
            val repository = PrayerRepository()
            val calendar = Calendar.getInstance()
            val result = repository.calculatePrayerTimes(
                latitude = latitude,
                longitude = longitude,
                method = method,
                madhab = madhab,
                calendar = calendar
            )

            // 3. Format Hijri date
            val todayHijri = HijriCalendar.getTodayHijri()
            val hijriStr = HijriCalendar.formatHijriDate(todayHijri)
            views.setTextViewText(R.id.widget_hijri_date, hijriStr)

            // 4. Update next prayer info
            val nextPrayer = result.nextPrayer
            if (nextPrayer != null) {
                views.setTextViewText(R.id.widget_next_prayer_name, nextPrayer.name)
                views.setTextViewText(R.id.widget_next_prayer_time, nextPrayer.formattedTime)

                // Format time remaining
                val remainingMs = result.timeRemainingMs
                val hours = remainingMs / 3600000
                val minutes = (remainingMs % 3600000) / 60000
                val remainingStr = when {
                    hours > 0 -> "in ${hours}h ${minutes}m"
                    minutes > 0 -> "in ${minutes}m"
                    else -> "now"
                }
                views.setTextViewText(R.id.widget_time_remaining, remainingStr)
            } else {
                views.setTextViewText(R.id.widget_next_prayer_name, "N/A")
                views.setTextViewText(R.id.widget_next_prayer_time, "--:--")
                views.setTextViewText(R.id.widget_time_remaining, "")
            }

        } catch (e: Exception) {
            views.setTextViewText(R.id.widget_next_prayer_name, "Error")
            views.setTextViewText(R.id.widget_next_prayer_time, "--:--")
            views.setTextViewText(R.id.widget_time_remaining, "")
        }

        // 5. Handle tap to open MainActivity
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            200,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_app_title, pendingIntent)
        views.setOnClickPendingIntent(R.id.widget_next_prayer_layout, pendingIntent)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        fun triggerUpdate(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisWidget = ComponentName(context, PrayerWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
                if (appWidgetIds.isNotEmpty()) {
                    val intent = Intent(context, PrayerWidgetProvider::class.java).apply {
                        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                    }
                    context.sendBroadcast(intent)
                }
            } catch (e: Exception) {
                // Fail silently
            }
        }
    }
}
