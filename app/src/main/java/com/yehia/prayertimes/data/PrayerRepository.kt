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
    FAJR, SUNRISE, DHUHR, ASR, MAGHRIB, ISHA
}

data class PrayerCalculationResult(
    val items: List<PrayerTimeItem>,
    val currentPrayer: PrayerTimeItem?,
    val nextPrayer: PrayerTimeItem?,
    val timeRemainingMs: Long,
    val qiblaDirection: Double
)

class PrayerRepository {

    private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        method: CalculationMethod = CalculationMethod.EGYPTIAN,
        madhab: Madhab = Madhab.SHAFI,
        calendar: Calendar = Calendar.getInstance()
    ): PrayerCalculationResult {
        val coordinates = Coordinates(latitude, longitude)
        val dateComponents = DateComponents.from(calendar.time)

        val params = method.parameters
        params.madhab = madhab

        val prayerTimes = PrayerTimes(coordinates, dateComponents, params)

        // Generate list of items
        val list = listOf(
            PrayerTimeItem(PrayerType.FAJR, "Fajr", prayerTimes.fajr, formatTime(prayerTimes.fajr)),
            PrayerTimeItem(PrayerType.SUNRISE, "Sunrise", prayerTimes.sunrise, formatTime(prayerTimes.sunrise)),
            PrayerTimeItem(PrayerType.DHUHR, "Dhuhr", prayerTimes.dhuhr, formatTime(prayerTimes.dhuhr)),
            PrayerTimeItem(PrayerType.ASR, "Asr", prayerTimes.asr, formatTime(prayerTimes.asr)),
            PrayerTimeItem(PrayerType.MAGHRIB, "Maghrib", prayerTimes.maghrib, formatTime(prayerTimes.maghrib)),
            PrayerTimeItem(PrayerType.ISHA, "Isha", prayerTimes.isha, formatTime(prayerTimes.isha))
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
                val tomorrow = Calendar.getInstance()
                tomorrow.add(Calendar.DAY_OF_YEAR, 1)
                val tomorrowComponents = DateComponents.from(tomorrow.time)
                val tomorrowTimes = PrayerTimes(coordinates, tomorrowComponents, params)
                remainingMs = tomorrowTimes.fajr.time - now.time
                // Update next pointer to tomorrow's Fajr time
                next = PrayerTimeItem(PrayerType.FAJR, "Fajr", tomorrowTimes.fajr, formatTime(tomorrowTimes.fajr))
            } else {
                remainingMs = nextTime.time - now.time
            }
        } else {
            // Default fallback calculation: next is tomorrow's Fajr
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrowComponents = DateComponents.from(tomorrow.time)
            val tomorrowTimes = PrayerTimes(coordinates, tomorrowComponents, params)
            next = PrayerTimeItem(PrayerType.FAJR, "Fajr", tomorrowTimes.fajr, formatTime(tomorrowTimes.fajr))
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

    private fun formatTime(date: Date): String {
        return timeFormatter.format(date)
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
