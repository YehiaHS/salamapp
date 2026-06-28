package com.yehia.prayertimes.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import java.util.Calendar

/**
 * Time-of-Day adaptive theme — inspired by Apple Weather's sky gradient system.
 *
 * 8 precisely-crafted keyframes covering the full 24-hour cycle:
 *
 *   00:00  → Deep Night (midnight indigo)
 *   04:30  → Pre-Dawn   (dusky violet)
 *   06:00  → Sunrise    (warm coral-gold)
 *   08:00  → Morning    (sky blue)
 *   12:00  → Midday     (clear azure)
 *   16:00  → Afternoon  (warm golden blue)
 *   18:30  → Sunset     (orange-pink-magenta)
 *   20:30  → Dusk       (purple-indigo)
 *   22:00  → Night      (midnight indigo, same as 00:00)
 *
 * Between keyframes all colors are linearly interpolated for seamless transitions.
 */
object TimeOfDayTheme {

    // ─── Color keyframes (minute-of-day → colors) ────────────────────────────

    /**
     * Each entry: minute-of-day → ThemePalette snapshot.
     * Sorted ascending. The system loops back from the last to the first.
     */
    private val keyframes: List<Pair<Int, ThemePalette>> by lazy {
        listOf(
            0    to deepNight(),          // 00:00 midnight
            270  to preDawn(),            // 04:30 just before Fajr
            360  to sunrise(),            // 06:00 sunrise
            480  to morning(),            // 08:00 morning
            720  to midday(),             // 12:00 noon
            960  to goldenAfternoon(),    // 16:00 golden afternoon
            1110 to sunset(),             // 18:30 Maghrib sunset
            1230 to dusk(),               // 20:30 evening dusk
            1320 to deepNight()           // 22:00 back to night
        )
    }

    /**
     * Computes the current palette by interpolating between the two surrounding keyframes.
     * Transitions update every minute via [currentMinuteOfDay].
     */
    fun getPalette(minuteOfDay: Int = currentMinuteOfDay()): ThemePalette {
        val frames = keyframes

        // Find surrounding keyframes
        var prevFrame = frames.last()
        var nextFrame = frames.first()

        for (i in frames.indices) {
            if (frames[i].first <= minuteOfDay) {
                prevFrame = frames[i]
                nextFrame = frames.getOrElse(i + 1) { frames[0].copy(first = 1440) }
            }
        }

        // Compute interpolation factor [0..1]
        val range = nextFrame.first - prevFrame.first
        val t = if (range <= 0) 0f else (minuteOfDay - prevFrame.first).toFloat() / range

        return lerpPalettes(prevFrame.second, nextFrame.second, t.coerceIn(0f, 1f))
    }

    fun getPaletteForTimeZone(tz: java.util.TimeZone): ThemePalette {
        return getPalette(currentMinuteOfDay(tz))
    }

    fun currentMinuteOfDay(tz: java.util.TimeZone = java.util.TimeZone.getDefault()): Int {
        val cal = Calendar.getInstance(tz)
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    }

    // ─── Palette lerp ────────────────────────────────────────────────────────

    private fun lerpPalettes(a: ThemePalette, b: ThemePalette, t: Float): ThemePalette = ThemePalette(
        background     = lerp(a.background, b.background, t),
        surface        = lerp(a.surface, b.surface, t),
        surfaceVariant = lerp(a.surfaceVariant, b.surfaceVariant, t),
        cardElevation1 = lerp(a.cardElevation1, b.cardElevation1, t),
        cardElevation2 = lerp(a.cardElevation2, b.cardElevation2, t),
        cardElevation3 = lerp(a.cardElevation3, b.cardElevation3, t),
        primary        = lerp(a.primary, b.primary, t),
        secondary      = lerp(a.secondary, b.secondary, t),
        accent         = lerp(a.accent, b.accent, t),
        onPrimary      = lerp(a.onPrimary, b.onPrimary, t),
        textPrimary    = lerp(a.textPrimary, b.textPrimary, t),
        textSecondary  = lerp(a.textSecondary, b.textSecondary, t),
        textMuted      = lerp(a.textMuted, b.textMuted, t),
        glowColor      = lerp(a.glowColor, b.glowColor, t),
        error          = lerp(a.error, b.error, t),
        outline        = lerp(a.outline, b.outline, t),
        scrim          = lerp(a.scrim, b.scrim, t),
        shimmer        = lerp(a.shimmer, b.shimmer, t),
        isLight        = if (t < 0.5f) a.isLight else b.isLight
    )

    // ─── Keyframe palettes ───────────────────────────────────────────────────

    /** 00:00 / 22:00 — Deep midnight indigo, starry & rich */
    private fun deepNight() = ThemePalette(
        background     = Color(0xFF06051A),   // Near-black deep indigo
        surface        = Color(0xFF0D0B2E),
        surfaceVariant = Color(0xFF16123D),
        cardElevation1 = Color(0xFF0D0B2E),
        cardElevation2 = Color(0xFF13104A),
        cardElevation3 = Color(0xFF1C175C),
        primary        = Color(0xFF8B9FFF),   // Soft periwinkle
        secondary      = Color(0xFF6B7FE8),
        accent         = Color(0xFFB8C8FF),
        onPrimary      = Color(0xFF0A0820),
        textPrimary    = Color(0xFFE8ECFF),
        textSecondary  = Color(0xFF9AA8D4),
        textMuted      = Color(0xFF5A6490),
        glowColor      = Color(0x308B9FFF),
        error          = Color(0xFFFF8A80),
        outline        = Color(0xFF2A2850),
        scrim          = Color(0xDD000016),
        shimmer        = Color(0x1A8B9FFF),
        isLight        = false
    )

    /** 04:30 — Pre-dawn, dusky violet, subtle warmth creeping in */
    private fun preDawn() = ThemePalette(
        background     = Color(0xFF0E0820),
        surface        = Color(0xFF18103A),
        surfaceVariant = Color(0xFF221848),
        cardElevation1 = Color(0xFF18103A),
        cardElevation2 = Color(0xFF1F1545),
        cardElevation3 = Color(0xFF2C2055),
        primary        = Color(0xFFB57AE0),   // Soft violet
        secondary      = Color(0xFF9B5EC8),
        accent         = Color(0xFFFFB3A7),   // Warm coral hint
        onPrimary      = Color(0xFF1A0A35),
        textPrimary    = Color(0xFFEDE0FF),
        textSecondary  = Color(0xFFB09AC8),
        textMuted      = Color(0xFF6E5885),
        glowColor      = Color(0x30B57AE0),
        error          = Color(0xFFFF8A80),
        outline        = Color(0xFF352648),
        scrim          = Color(0xCC080015),
        shimmer        = Color(0x1AB57AE0),
        isLight        = false
    )

    /** 06:00 — Sunrise, warm coral-orange-gold, soft and luminous */
    private fun sunrise() = ThemePalette(
        background     = Color(0xFF1A0D2E),   // Deep purple base
        surface        = Color(0xFF2A1240),
        surfaceVariant = Color(0xFF3A1A50),
        cardElevation1 = Color(0xFF2A1240),
        cardElevation2 = Color(0xFF351848),
        cardElevation3 = Color(0xFF44225A),
        primary        = Color(0xFFFF8C6B),   // Warm coral
        secondary      = Color(0xFFFFB347),   // Amber-gold
        accent         = Color(0xFFFF6DA0),   // Pink-rose
        onPrimary      = Color(0xFF2A0A00),
        textPrimary    = Color(0xFFFFF0E8),
        textSecondary  = Color(0xFFD4A898),
        textMuted      = Color(0xFF8A6060),
        glowColor      = Color(0x40FF8C6B),
        error          = Color(0xFFFF6B6B),
        outline        = Color(0xFF4A2A38),
        scrim          = Color(0xCC1A0010),
        shimmer        = Color(0x1AFF8C6B),
        isLight        = false
    )

    /** 08:00 — Bright morning, clear sky blue, fresh and energizing */
    private fun morning() = ThemePalette(
        background     = Color(0xFF0A1F3E),   // Deep sky navy
        surface        = Color(0xFF102A50),
        surfaceVariant = Color(0xFF163560),
        cardElevation1 = Color(0xFF102A50),
        cardElevation2 = Color(0xFF143260),
        cardElevation3 = Color(0xFF1D4070),
        primary        = Color(0xFF4FC3F7),   // Clear sky blue
        secondary      = Color(0xFF29B6F6),
        accent         = Color(0xFF80DEEA),   // Light cyan
        onPrimary      = Color(0xFF001C38),
        textPrimary    = Color(0xFFE8F4FF),
        textSecondary  = Color(0xFF90C8E8),
        textMuted      = Color(0xFF4A7A9A),
        glowColor      = Color(0x304FC3F7),
        error          = Color(0xFFFF8A80),
        outline        = Color(0xFF1E3D5A),
        scrim          = Color(0xCC000D1E),
        shimmer        = Color(0x1A4FC3F7),
        isLight        = false
    )

    /** 12:00 — Midday, vivid azure with crystalline clarity */
    private fun midday() = ThemePalette(
        background     = Color(0xFF051525),   // Deep ocean blue
        surface        = Color(0xFF0A1F35),
        surfaceVariant = Color(0xFF102845),
        cardElevation1 = Color(0xFF0A1F35),
        cardElevation2 = Color(0xFF0E2840),
        cardElevation3 = Color(0xFF173350),
        primary        = Color(0xFF00BCD4),   // Vibrant cyan
        secondary      = Color(0xFF0097A7),
        accent         = Color(0xFF4DD0E1),
        onPrimary      = Color(0xFF001820),
        textPrimary    = Color(0xFFE0F7FA),
        textSecondary  = Color(0xFF80C8D0),
        textMuted      = Color(0xFF3D7880),
        glowColor      = Color(0x3000BCD4),
        error          = Color(0xFFFF7043),
        outline        = Color(0xFF153040),
        scrim          = Color(0xCC00080F),
        shimmer        = Color(0x1A00BCD4),
        isLight        = false
    )

    /** 16:00 — Golden afternoon, warm amber-tinted sky */
    private fun goldenAfternoon() = ThemePalette(
        background     = Color(0xFF120A00),   // Deep amber-tinted dark
        surface        = Color(0xFF1E1200),
        surfaceVariant = Color(0xFF2A1A06),
        cardElevation1 = Color(0xFF1E1200),
        cardElevation2 = Color(0xFF281808),
        cardElevation3 = Color(0xFF342010),
        primary        = Color(0xFFFFB74D),   // Warm amber
        secondary      = Color(0xFFFF9800),
        accent         = Color(0xFFFFCC80),   // Soft gold
        onPrimary      = Color(0xFF1A0900),
        textPrimary    = Color(0xFFFFF8E8),
        textSecondary  = Color(0xFFD4B080),
        textMuted      = Color(0xFF8A6830),
        glowColor      = Color(0x40FFB74D),
        error          = Color(0xFFFF5252),
        outline        = Color(0xFF3A2800),
        scrim          = Color(0xCC0A0500),
        shimmer        = Color(0x1AFFB74D),
        isLight        = false
    )

    /** 18:30 — Sunset, fiery orange-pink-magenta, dramatic */
    private fun sunset() = ThemePalette(
        background     = Color(0xFF150820),   // Deep dusk purple
        surface        = Color(0xFF210D30),
        surfaceVariant = Color(0xFF2E1240),
        cardElevation1 = Color(0xFF210D30),
        cardElevation2 = Color(0xFF2A1038),
        cardElevation3 = Color(0xFF381548),
        primary        = Color(0xFFFF6B6B),   // Vibrant coral-red
        secondary      = Color(0xFFFF8E53),   // Warm orange
        accent         = Color(0xFFFF4D8D),   // Hot pink
        onPrimary      = Color(0xFF200000),
        textPrimary    = Color(0xFFFFF0F4),
        textSecondary  = Color(0xFFD49BAA),
        textMuted      = Color(0xFF8A4A5A),
        glowColor      = Color(0x40FF6B6B),
        error          = Color(0xFFFF5252),
        outline        = Color(0xFF40183A),
        scrim          = Color(0xCC100010),
        shimmer        = Color(0x1AFF6B6B),
        isLight        = false
    )

    /** 20:30 — Evening dusk, transitioning purple-indigo */
    private fun dusk() = ThemePalette(
        background     = Color(0xFF0C0820),
        surface        = Color(0xFF140F35),
        surfaceVariant = Color(0xFF1E1545),
        cardElevation1 = Color(0xFF140F35),
        cardElevation2 = Color(0xFF1C1442),
        cardElevation3 = Color(0xFF261C52),
        primary        = Color(0xFF9C7AE0),   // Soft purple
        secondary      = Color(0xFF7B5CC8),
        accent         = Color(0xFFE87ABF),   // Dusty rose
        onPrimary      = Color(0xFF150828),
        textPrimary    = Color(0xFFEDE0FF),
        textSecondary  = Color(0xFFA890C8),
        textMuted      = Color(0xFF6050A0),
        glowColor      = Color(0x309C7AE0),
        error          = Color(0xFFFF8A80),
        outline        = Color(0xFF2E2248),
        scrim          = Color(0xCC080010),
        shimmer        = Color(0x1A9C7AE0),
        isLight        = false
    )
}
