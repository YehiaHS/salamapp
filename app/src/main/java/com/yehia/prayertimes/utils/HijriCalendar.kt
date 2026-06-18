package com.yehia.prayertimes.utils

import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

/**
 * Hijri calendar utility implementing the Kuwaiti/Tabular Islamic calendar algorithm
 * for converting Gregorian dates to Hijri (Islamic) dates.
 */
object HijriCalendar {

    data class HijriDate(
        val year: Int,
        val month: Int,
        val day: Int
    )

    /**
     * Converts a Gregorian date to Hijri using the Kuwaiti algorithm.
     *
     * This is the well-known tabular Islamic calendar approximation that uses
     * a fixed arithmetic scheme. It matches the Umm al-Qura calendar to within
     * ±1 day for most dates.
     */
    fun toHijri(year: Int, month: Int, day: Int): HijriDate {
        // Step 1: Compute the Julian Day Number (JDN) for the given Gregorian date
        val jdn = gregorianToJdn(year, month, day)

        // Step 2: Convert JDN to Hijri using the Kuwaiti algorithm
        return jdnToHijri(jdn)
    }

    /**
     * Returns Arabic month name for the given Hijri month (1-12).
     */
    fun getHijriMonthName(month: Int): String {
        return when (month) {
            1 -> "محرم"
            2 -> "صفر"
            3 -> "ربيع الأول"
            4 -> "ربيع الثاني"
            5 -> "جمادى الأولى"
            6 -> "جمادى الآخرة"
            7 -> "رجب"
            8 -> "شعبان"
            9 -> "رمضان"
            10 -> "شوال"
            11 -> "ذو القعدة"
            12 -> "ذو الحجة"
            else -> ""
        }
    }

    /**
     * Returns English transliteration of the Hijri month name for the given month (1-12).
     */
    fun getHijriMonthNameEnglish(month: Int): String {
        return when (month) {
            1 -> "Muharram"
            2 -> "Safar"
            3 -> "Rabi al-Awwal"
            4 -> "Rabi ath-Thani"
            5 -> "Jumada al-Ula"
            6 -> "Jumada al-Akhirah"
            7 -> "Rajab"
            8 -> "Sha'ban"
            9 -> "Ramadan"
            10 -> "Shawwal"
            11 -> "Dhul-Qa'dah"
            12 -> "Dhul-Hijjah"
            else -> ""
        }
    }

    /**
     * Formats a HijriDate into a human-readable string, e.g. "15 Ramadan 1447"
     */
    fun formatHijriDate(date: HijriDate): String {
        val monthName = getHijriMonthNameEnglish(date.month)
        return "${date.day} $monthName ${date.year}"
    }

    /**
     * Gets today's Hijri date based on the system calendar.
     */
    fun getTodayHijri(): HijriDate {
        val cal = Calendar.getInstance()
        return toHijri(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1, // Calendar.MONTH is 0-indexed
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    // ──────────────────────────────────────────────────
    // Internal conversion routines
    // ──────────────────────────────────────────────────

    /**
     * Converts Gregorian date to Julian Day Number (JDN).
     * Uses the standard astronomical algorithm.
     */
    private fun gregorianToJdn(year: Int, month: Int, day: Int): Long {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3

        // Gregorian calendar JDN formula
        val jdn = day.toLong() +
                (153L * m + 2L) / 5L +
                365L * y +
                y / 4L -
                y / 100L +
                y / 400L -
                32045L

        return jdn
    }

    /**
     * Converts a Julian Day Number to a Hijri date using the Kuwaiti algorithm.
     *
     * The Kuwaiti algorithm is a well-known tabular Islamic calendar method
     * that uses a 30-year cycle with 11 leap years. Each cycle has 10631 days.
     * Odd months have 30 days, even months have 29 days, except in leap years
     * where the 12th month (Dhul-Hijjah) has 30 days.
     */
    private fun jdnToHijri(jdn: Long): HijriDate {
        // Hijri epoch in Julian Day Number: July 16, 622 CE (Julian) = July 19, 622 CE (Gregorian)
        // The epoch JDN is 1948439.5 truncated to 1948440 for integer arithmetic
        val hijriEpochJdn = 1948440L

        // Shift from the epoch
        val l = jdn - hijriEpochJdn + 10632L
        val n = ((l - 1L) / 10631L)
        val remainder = l - 10631L * n + 354L

        val j = ((10985L - remainder) / 5316L) *
                ((50L * remainder) / 17719L) +
                (remainder / 5670L) *
                ((43L * remainder) / 15238L)

        val adjustedRemainder = remainder -
                ((30L - j) / 15L) *
                ((17719L * j) / 50L) -
                (j / 16L) *
                ((15238L * j) / 43L) +
                29L

        val hijriMonth = ((24L * adjustedRemainder) / 709L)
        val hijriDay = adjustedRemainder - ((709L * hijriMonth) / 24L)
        val hijriYear = 30L * n + j - 30L

        return HijriDate(
            year = hijriYear.toInt(),
            month = hijriMonth.toInt(),
            day = hijriDay.toInt()
        )
    }
}
