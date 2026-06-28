package com.yehia.prayertimes.data

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.Prayer
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.Qibla
import com.batoulapps.adhan.data.DateComponents
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class PrayerTimeItem(
    val type: PrayerType,
    val name: String,
    val time: Date,
    val formattedTime: String
)

enum class PrayerType {
    FAJR, SUNRISE, DHUHR, ASR, MAGHRIB, ISHA, QIYAM
}

data class PrayerCalculationResult(
    val items: List<PrayerTimeItem>,
    val currentPrayer: PrayerTimeItem?,
    val nextPrayer: PrayerTimeItem?,
    val timeRemainingMs: Long,
    val qiblaDirection: Double
)

class PrayerRepository {

    private fun getTimeZoneForCoordinate(longitude: Double): TimeZone {
        val offsetHours = Math.round(longitude / 15.0).toInt()
        val gmtId = String.format(Locale.US, "GMT%+d", offsetHours)
        return TimeZone.getTimeZone(gmtId)
    }

    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        method: CalculationMethod = CalculationMethod.EGYPTIAN,
        madhab: Madhab = Madhab.SHAFI,
        calendar: Calendar = Calendar.getInstance()
    ): PrayerCalculationResult {
        val coordinates = Coordinates(latitude, longitude)
        val timeZone = getTimeZoneForCoordinate(longitude)
        
        calendar.timeZone = timeZone
        val dateComponents = DateComponents(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val params = method.parameters
        params.madhab = madhab

        val prayerTimes = PrayerTimes(coordinates, dateComponents, params)

        // Calculate tomorrow's Fajr to find the night duration for Qiyam Al-Layl
        val tomorrow = Calendar.getInstance()
        tomorrow.timeZone = timeZone
        tomorrow.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowComponents = DateComponents(
            tomorrow.get(Calendar.YEAR),
            tomorrow.get(Calendar.MONTH) + 1,
            tomorrow.get(Calendar.DAY_OF_MONTH)
        )
        val tomorrowTimes = PrayerTimes(coordinates, tomorrowComponents, params)

        val nightDuration = tomorrowTimes.fajr.time - prayerTimes.maghrib.time
        val lastThirdStart = Date(prayerTimes.maghrib.time + (nightDuration * 2) / 3)

        // Generate list of items
        val list = listOf(
            PrayerTimeItem(PrayerType.FAJR, "Fajr", prayerTimes.fajr, formatTime(prayerTimes.fajr, timeZone)),
            PrayerTimeItem(PrayerType.SUNRISE, "Sunrise", prayerTimes.sunrise, formatTime(prayerTimes.sunrise, timeZone)),
            PrayerTimeItem(PrayerType.DHUHR, "Dhuhr", prayerTimes.dhuhr, formatTime(prayerTimes.dhuhr, timeZone)),
            PrayerTimeItem(PrayerType.ASR, "Asr", prayerTimes.asr, formatTime(prayerTimes.asr, timeZone)),
            PrayerTimeItem(PrayerType.MAGHRIB, "Maghrib", prayerTimes.maghrib, formatTime(prayerTimes.maghrib, timeZone)),
            PrayerTimeItem(PrayerType.ISHA, "Isha", prayerTimes.isha, formatTime(prayerTimes.isha, timeZone)),
            PrayerTimeItem(PrayerType.QIYAM, "Qiyam", lastThirdStart, formatTime(lastThirdStart, timeZone))
        )

        // Find current and next prayer
        val now = Date()
        var current: PrayerTimeItem? = null
        var next: PrayerTimeItem? = null

        val currentPrayerType = prayerTimes.currentPrayer()
        val nextPrayerType = prayerTimes.nextPrayer()

        // Map adhan library's native enum back to our model
        current = list.find { it.type == mapAdhanPrayer(currentPrayerType) }
        next = list.find { it.type == mapAdhanPrayer(nextPrayerType) }

        // Edge case handling: If after Isha or before Fajr of next day
        var remainingMs: Long = 0
        if (next != null) {
            val nextTime = next.time
            // If next prayer is tomorrow's Fajr (indicated by adhan library returning next prayer as Fajr but it being earlier today than now)
            if (next.type == PrayerType.FAJR && nextTime.before(now)) {
                remainingMs = tomorrowTimes.fajr.time - now.time
                // Update next pointer to tomorrow's Fajr time
                next = PrayerTimeItem(PrayerType.FAJR, "Fajr", tomorrowTimes.fajr, formatTime(tomorrowTimes.fajr, timeZone))
            } else {
                remainingMs = nextTime.time - now.time
            }
        } else {
            // Default fallback calculation: next is tomorrow's Fajr
            next = PrayerTimeItem(PrayerType.FAJR, "Fajr", tomorrowTimes.fajr, formatTime(tomorrowTimes.fajr, timeZone))
            remainingMs = tomorrowTimes.fajr.time - now.time
        }

        // If current prayer is NONE, we are typically in the post-Isha period (so Fajr is next and current is Isha)
        if (current == null) {
            current = list.find { it.type == PrayerType.ISHA }
        }

        // Qibla Direction computation
        val qibla = Qibla(coordinates)
        val qiblaDirection = qibla.direction

        return PrayerCalculationResult(
            items = list,
            currentPrayer = current,
            nextPrayer = next,
            timeRemainingMs = remainingMs.coerceAtLeast(0),
            qiblaDirection = qiblaDirection
        )
    }

    private fun formatTime(date: Date, timeZone: TimeZone): String {
        val lang = com.yehia.prayertimes.utils.LanguageManager.currentLang.value
        val locale = if (lang == "ar") Locale("ar") else Locale.US
        val sdf = SimpleDateFormat("hh:mm a", locale)
        sdf.timeZone = timeZone
        val formatted = sdf.format(date)
        return if (lang == "ar") {
            formatted.replace("AM", "ص").replace("PM", "م")
        } else {
            formatted
        }
    }

    private fun mapAdhanPrayer(prayer: Prayer): PrayerType? {
        return when (prayer) {
            Prayer.FAJR -> PrayerType.FAJR
            Prayer.SUNRISE -> PrayerType.SUNRISE
            Prayer.DHUHR -> PrayerType.DHUHR
            Prayer.ASR -> PrayerType.ASR
            Prayer.MAGHRIB -> PrayerType.MAGHRIB
            Prayer.ISHA -> PrayerType.ISHA
            Prayer.NONE -> null
        }
    }
}
