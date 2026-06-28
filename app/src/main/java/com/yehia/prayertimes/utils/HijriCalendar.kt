package com.yehia.prayertimes.utils

import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

/**
 * Hijri calendar utility using the native java.time.chrono.HijrahDate (Umm al-Qura variant)
 * for accurate Gregorian to Hijri conversions.
 */
object HijriCalendar {

    data class HijriDate(
        val year: Int,
        val month: Int,
        val day: Int
    )

    /**
     * Converts a Gregorian date to Hijri using native HijrahChronology (Umm al-Qura).
     */
    fun toHijri(year: Int, month: Int, day: Int): HijriDate {
        return try {
            val localDate = LocalDate.of(year, month, day)
            val hijrahDate = HijrahDate.from(localDate)
            HijriDate(
                year = hijrahDate.get(ChronoField.YEAR),
                month = hijrahDate.get(ChronoField.MONTH_OF_YEAR),
                day = hijrahDate.get(ChronoField.DAY_OF_MONTH)
            )
        } catch (e: Exception) {
            // Fallback in case of conversion error
            HijriDate(year, month, day)
        }
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
     * Formats a HijriDate into a human-readable string, e.g. "15 Ramadan 1447" or Arabic equivalent
     */
    fun formatHijriDate(date: HijriDate, lang: String = LanguageManager.currentLang.value): String {
        return if (lang == "ar") {
            val raw = "${date.day} ${getHijriMonthName(date.month)} ${date.year}"
            raw.map { char ->
                when (char) {
                    '0' -> '٠'
                    '1' -> '١'
                    '2' -> '٢'
                    '3' -> '٣'
                    '4' -> '٤'
                    '5' -> '٥'
                    '6' -> '٦'
                    '7' -> '٧'
                    '8' -> '٨'
                    '9' -> '٩'
                    else -> char
                }
            }.joinToString("")
        } else {
            val monthName = getHijriMonthNameEnglish(date.month)
            "${date.day} $monthName ${date.year}"
        }
    }

    /**
     * Gets today's Hijri date based on the specified TimeZone (defaults to system default).
     */
    fun getTodayHijri(timeZone: TimeZone = TimeZone.getDefault()): HijriDate {
        val cal = Calendar.getInstance(timeZone)
        return toHijri(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1, // Calendar.MONTH is 0-indexed
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * Converts a Hijri date back to Julian Day Number (JDN).
     */
    fun hijriToJdn(year: Int, month: Int, day: Int): Long {
        return try {
            val hijrahDate = HijrahDate.of(year, month, day)
            val localDate = LocalDate.from(hijrahDate)
            gregorianToJdn(localDate.year, localDate.monthValue, localDate.dayOfMonth)
        } catch (e: Exception) {
            // Fallback using standard mathematical formula
            val hDate = HijriDate(year, month, day)
            val fallbackDate = hijriToGregorianDate(hDate)
            val cal = Calendar.getInstance()
            cal.time = fallbackDate
            gregorianToJdn(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        }
    }

    /**
     * Converts Gregorian date to Julian Day Number (JDN).
     */
    private fun gregorianToJdn(year: Int, month: Int, day: Int): Long {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        return day.toLong() +
                (153L * m + 2L) / 5L +
                365L * y +
                y / 4L -
                y / 100L +
                y / 400L -
                32045L
    }

    /**
     * Converts a HijriDate to java.util.Date representation in Gregorian.
     */
    fun hijriToGregorianDate(hijri: HijriDate): Date {
        return try {
            val hijrahDate = HijrahDate.of(hijri.year, hijri.month, hijri.day)
            val localDate = LocalDate.from(hijrahDate)
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, localDate.year)
            cal.set(Calendar.MONTH, localDate.monthValue - 1)
            cal.set(Calendar.DAY_OF_MONTH, localDate.dayOfMonth)
            cal.set(Calendar.HOUR_OF_DAY, 12)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.time
        } catch (e: Exception) {
            val cal = Calendar.getInstance()
            cal.time
        }
    }

    /**
     * Returns the length of the specified Hijri month.
     */
    fun getDaysInMonth(year: Int, month: Int): Int {
        return try {
            val hijrahDate = HijrahDate.of(year, month, 1)
            hijrahDate.lengthOfMonth()
        } catch (e: Exception) {
            // Fallback to tabular rules if error
            val isLeapYear = (11 * year + 14) % 30 < 11
            when (month) {
                1, 3, 5, 7, 9, 11 -> 30
                2, 4, 6, 8, 10 -> 29
                12 -> if (isLeapYear) 30 else 29
                else -> 29
            }
        }
    }
}
