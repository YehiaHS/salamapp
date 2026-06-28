package com.yehia.prayertimes.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.staticCompositionLocalOf

val LocalThemePalette = staticCompositionLocalOf { SalamTwilightPalette }

data class ThemePalette(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val cardElevation1: Color,   // Lowest card level (subtle)
    val cardElevation2: Color,   // Mid card level (default cards)
    val cardElevation3: Color,   // Highest card level (hero/active cards)
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val onPrimary: Color,        // Text on primary-colored surfaces
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val glowColor: Color,
    val error: Color,
    val outline: Color,          // Subtle borders
    val scrim: Color,            // Overlay/modal backdrop
    val shimmer: Color,          // Loading shimmer highlight
    val isLight: Boolean = false // Light theme flag for status bar
)

// ─────────────────────────────────────────────────
// ILLUSTRATION PALETTE — Derived from theme for sky/hero illustrations
// ─────────────────────────────────────────────────
data class IllustrationPalette(
    val skyGradientTop: Color,
    val skyGradientBottom: Color,
    val moonColor: Color,
    val starColor: Color,
    val buildingColor: Color,
    val glowColor: Color,
    val badgeBg: Color,     // For teal-like status badges
    val badgeText: Color    // Text on badge
)

fun ThemePalette.toIllustrationPalette(): IllustrationPalette {
    // Determine dynamic times to choose colors matching sunrise/sunset/daylight/nighttime
    val cal = java.util.Calendar.getInstance()
    val hr = cal.get(java.util.Calendar.HOUR_OF_DAY)
    val min = cal.get(java.util.Calendar.MINUTE)
    val timeMinutes = hr * 60 + min

    // 04:30 to 06:00 is sunrise
    // 06:00 to 17:30 is daylight
    // 17:30 to 19:30 is sunset
    // 19:30 to 04:30 is nighttime
    val (skyTop, skyBottom, starAlpha) = when (timeMinutes) {
        in 270..360 -> { // Sunrise: soft orange/pink to gold
            Triple(Color(0xFFFC5C7D), Color(0xFF6A82FB), 0.1f)
        }
        in 361..1050 -> { // Daylight: bright morning/afternoon sky
            Triple(Color(0xFF4A90E2), Color(0xFF50E3C2), 0.0f)
        }
        in 1051..1170 -> { // Sunset: deep purple/crimson to warm orange
            Triple(Color(0xFF3E2A75), Color(0xFFFF5E62), 0.1f)
        }
        else -> { // Nighttime: deep cosmic blue/purple
            Triple(Color(0xFF0F0B1E), Color(0xFF1E1430), 0.8f)
        }
    }

    return if (isLight) {
        IllustrationPalette(
            skyGradientTop = skyTop.copy(alpha = 0.85f),
            skyGradientBottom = skyBottom.copy(alpha = 0.6f),
            moonColor = Color(0xFFF4EFEA),
            starColor = Color(0xFFFFD54F).copy(alpha = starAlpha),
            buildingColor = primary.copy(alpha = 0.25f),
            glowColor = primary.copy(alpha = 0.15f),
            badgeBg = secondary.copy(alpha = 0.12f),
            badgeText = secondary
        )
    } else {
        IllustrationPalette(
            skyGradientTop = skyTop.copy(alpha = 0.55f),
            skyGradientBottom = skyBottom.copy(alpha = 0.35f),
            moonColor = textPrimary.copy(alpha = 0.9f),
            starColor = Color(0xFFFFD54F).copy(alpha = starAlpha),
            buildingColor = primary.copy(alpha = 0.18f),
            glowColor = primary.copy(alpha = 0.1f),
            badgeBg = secondary.copy(alpha = 0.15f),
            badgeText = secondary
        )
    }
}

// ─────────────────────────────────────────────────
// READING THEME — Independent palettes for Quran reader
// ─────────────────────────────────────────────────
data class ReadingTheme(
    val name: String,
    val background: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val surface: Color
)

val readingThemes = listOf(
    ReadingTheme(
        name = "Parchment",
        background = Color(0xFFFAF4E8),
        textPrimary = Color(0xFF2C1810),
        textSecondary = Color(0xFF6B4F3A),
        accent = Color(0xFF8B6914),
        surface = Color(0xFFF0E6D2)
    ),
    ReadingTheme(
        name = "Emerald",
        background = Color(0xFF0A1F1A),
        textPrimary = Color(0xFFE8F5E8),
        textSecondary = Color(0xFF8BC8A0),
        accent = Color(0xFF00E5A3),
        surface = Color(0xFF0F2F24)
    ),
    ReadingTheme(
        name = "Night",
        background = Color(0xFF0A0A14),
        textPrimary = Color(0xFFE8E4F0),
        textSecondary = Color(0xFF9088A8),
        accent = Color(0xFFBB86FC),
        surface = Color(0xFF14142A)
    ),
    ReadingTheme(
        name = "Cream",
        background = Color(0xFFFFF8F0),
        textPrimary = Color(0xFF1A1A2E),
        textSecondary = Color(0xFF5A5A7A),
        accent = Color(0xFF3E2A75),
        surface = Color(0xFFF5EDE0)
    )
)

// ─────────────────────────────────────────────────
// THEME PALETTES
// ─────────────────────────────────────────────────

val SalamTwilightPalette = ThemePalette(
    background = Color(0xFF09080F),     // Very dark violet/black
    surface = Color(0xFF131124),        // Deep dark violet surface
    surfaceVariant = Color(0xFF1C1834), // Slightly lighter violet
    cardElevation1 = Color(0xFF131124),
    cardElevation2 = Color(0xFF161426), // Card base matching the mockup
    cardElevation3 = Color(0xFF201C38), // Hover/Active card
    primary = Color(0xFFA78BFA),        // Rich lavender/violet matching Fajr circle indicator
    secondary = Color(0xFF8B5CF6),      // Vivid violet
    accent = Color(0xFFC084FC),         // Soft purple accent
    onPrimary = Color(0xFF09080F),      // Contrast text on primary
    textPrimary = Color(0xFFFFFFFF),    // Clean white text
    textSecondary = Color(0xFF9D9BAE),  // Muted gray-violet
    textMuted = Color(0xFF5C5A6E),      // Darker muted text
    glowColor = Color(0x26A78BFA),
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF201C38),        // Thin border color matching mockup cards
    scrim = Color(0xCC000000),
    shimmer = Color(0x1AFFFFFF),
    isLight = false
)

val EmeraldNightPalette = ThemePalette(
    background = Color(0xFF060A12),
    surface = Color(0xFF0E1520),
    surfaceVariant = Color(0xFF182230),
    cardElevation1 = Color(0xFF0E1520),
    cardElevation2 = Color(0xFF131D2C),
    cardElevation3 = Color(0xFF1A2738),
    primary = Color(0xFF00E5A3),
    secondary = Color(0xFF0D9488),
    accent = Color(0xFFF59E0B),
    onPrimary = Color(0xFF00201A),
    textPrimary = Color(0xFFF1F5F9),
    textSecondary = Color(0xFF8B9DB8),
    textMuted = Color(0xFF4F6378),
    glowColor = Color(0x2600E5A3),
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF1E3044),
    scrim = Color(0xCC000000),
    shimmer = Color(0x1AFFFFFF)
)

val RoyalGoldPalette = ThemePalette(
    background = Color(0xFF08080F),
    surface = Color(0xFF101018),
    surfaceVariant = Color(0xFF1A1A28),
    cardElevation1 = Color(0xFF101018),
    cardElevation2 = Color(0xFF151522),
    cardElevation3 = Color(0xFF1E1E30),
    primary = Color(0xFFFFD54F),
    secondary = Color(0xFFC9A227),
    accent = Color(0xFFE74C3C),
    onPrimary = Color(0xFF1A1400),
    textPrimary = Color(0xFFF5F3EE),
    textSecondary = Color(0xFF9A96A8),
    textMuted = Color(0xFF5E5C6E),
    glowColor = Color(0x26FFD54F),
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF2A2840),
    scrim = Color(0xCC000000),
    shimmer = Color(0x1AFFFFFF)
)

val DesertRosePalette = ThemePalette(
    background = Color(0xFF110B0B),
    surface = Color(0xFF1E1414),
    surfaceVariant = Color(0xFF2E2020),
    cardElevation1 = Color(0xFF1E1414),
    cardElevation2 = Color(0xFF241818),
    cardElevation3 = Color(0xFF2E2020),
    primary = Color(0xFFEDA0C0),
    secondary = Color(0xFFBB6B8A),
    accent = Color(0xFFD4A574),
    onPrimary = Color(0xFF2A0E18),
    textPrimary = Color(0xFFFAF0F0),
    textSecondary = Color(0xFFAA8E96),
    textMuted = Color(0xFF6E5860),
    glowColor = Color(0x26EDA0C0),
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF3A2830),
    scrim = Color(0xCC000000),
    shimmer = Color(0x1AFFFFFF)
)

// Improved: warm coral accent for complementary contrast
val OceanBluePalette = ThemePalette(
    background = Color(0xFF050B10),
    surface = Color(0xFF0A1520),
    surfaceVariant = Color(0xFF12202E),
    cardElevation1 = Color(0xFF0A1520),
    cardElevation2 = Color(0xFF0F1C2A),
    cardElevation3 = Color(0xFF152636),
    primary = Color(0xFF00B4D8),
    secondary = Color(0xFF0077B6),
    accent = Color(0xFFFF8A65),        // Warm coral instead of monochrome blue
    onPrimary = Color(0xFF001820),
    textPrimary = Color(0xFFEEF4FA),
    textSecondary = Color(0xFF7EA0B8),
    textMuted = Color(0xFF4A6E82),
    glowColor = Color(0x2600B4D8),
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF183040),
    scrim = Color(0xCC000000),
    shimmer = Color(0x1AFFFFFF)
)

// Improved: richer purple-indigo with gold accent for distinctiveness
val MidnightPurplePalette = ThemePalette(
    background = Color(0xFF0A0810),
    surface = Color(0xFF14101E),
    surfaceVariant = Color(0xFF201830),
    cardElevation1 = Color(0xFF14101E),
    cardElevation2 = Color(0xFF1A1428),
    cardElevation3 = Color(0xFF221A34),
    primary = Color(0xFFA78BFA),       // Richer indigo-violet
    secondary = Color(0xFF7C4DFF),
    accent = Color(0xFFFBBF24),        // Gold accent instead of generic rose
    onPrimary = Color(0xFF1A0830),
    textPrimary = Color(0xFFF3EEF8),
    textSecondary = Color(0xFF9886AA),
    textMuted = Color(0xFF615074),
    glowColor = Color(0x26A78BFA),
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF2E2044),
    scrim = Color(0xCC000000),
    shimmer = Color(0x1AFFFFFF)
)

// Pantone Color of the Year 2024: Peach Fuzz
val PeachFuzzPalette = ThemePalette(
    background = Color(0xFFFFF7F2),
    surface = Color(0xFFFFEFE6),
    surfaceVariant = Color(0xFFFCDED0),
    cardElevation1 = Color(0xFFFFEFE6),
    cardElevation2 = Color(0xFFFFF3EC),
    cardElevation3 = Color(0xFFFAD1C0),
    primary = Color(0xFFFF9E79),
    secondary = Color(0xFFFE7A47),
    accent = Color(0xFFD35400),
    onPrimary = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF4A2711),
    textSecondary = Color(0xFF8A604A),
    textMuted = Color(0xFFB59381),
    glowColor = Color(0x26FF9E79),
    error = Color(0xFFB3261E),
    outline = Color(0xFFF1D1C2),
    scrim = Color(0x66000000),
    shimmer = Color(0x0A000000),
    isLight = true
)

// Pantone Color of the Year 2023: Viva Magenta
val VivaMagentaPalette = ThemePalette(
    background = Color(0xFF12080B),
    surface = Color(0xFF200F14),
    surfaceVariant = Color(0xFF30171E),
    cardElevation1 = Color(0xFF200F14),
    cardElevation2 = Color(0xFF261219),
    cardElevation3 = Color(0xFF331920),
    primary = Color(0xFFFF5E84),
    secondary = Color(0xFFBB2649),
    accent = Color(0xFFFFB85C),
    onPrimary = Color(0xFF12080B),
    textPrimary = Color(0xFFFFF2F5),
    textSecondary = Color(0xFFC79EA9),
    textMuted = Color(0xFF7D5F66),
    glowColor = Color(0x26FF5E84),
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF3D1F27),
    scrim = Color(0xCC000000),
    shimmer = Color(0x1AFFFFFF),
    isLight = false
)

// Pantone Color of the Year 2022: Very Peri
val VeryPeriPalette = ThemePalette(
    background = Color(0xFF0C0C14),
    surface = Color(0xFF161626),
    surfaceVariant = Color(0xFF222238),
    cardElevation1 = Color(0xFF161626),
    cardElevation2 = Color(0xFF1B1B30),
    cardElevation3 = Color(0xFF262640),
    primary = Color(0xFF8F93FF),
    secondary = Color(0xFF6667AB),
    accent = Color(0xFFFFB259),
    onPrimary = Color(0xFF0C0C14),
    textPrimary = Color(0xFFF2F3FC),
    textSecondary = Color(0xFF9EA2C2),
    textMuted = Color(0xFF616480),
    glowColor = Color(0x268F93FF),
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF2A2B47),
    scrim = Color(0xCC000000),
    shimmer = Color(0x1AFFFFFF),
    isLight = false
)

// ─────────────────────────────────────────────────
// APP THEME ENUM
// ─────────────────────────────────────────────────
enum class AppTheme(val displayName: String) {
    TIME_OF_DAY("Time of Day"),
    SALAM_TWILIGHT("Salam Twilight"),
    EMERALD_NIGHT("Emerald Night"),
    ROYAL_GOLD("Royal Gold"),
    DESERT_ROSE("Desert Rose"),
    OCEAN_BLUE("Ocean Blue"),
    MIDNIGHT_PURPLE("Midnight Purple"),
    PEACH_FUZZ("Peach Fuzz (2024)"),
    VIVA_MAGENTA("Viva Magenta (2023)"),
    VERY_PERI("Very Peri (2022)"),
    DYNAMIC("Dynamic Wallpaper"),
    CUSTOM("Custom");

    fun getPalette(): ThemePalette {
        return when (this) {
            TIME_OF_DAY    -> TimeOfDayTheme.getPalette()
            SALAM_TWILIGHT -> SalamTwilightPalette
            EMERALD_NIGHT  -> EmeraldNightPalette
            ROYAL_GOLD     -> RoyalGoldPalette
            DESERT_ROSE    -> DesertRosePalette
            OCEAN_BLUE     -> OceanBluePalette
            MIDNIGHT_PURPLE-> MidnightPurplePalette
            PEACH_FUZZ     -> PeachFuzzPalette
            VIVA_MAGENTA   -> VivaMagentaPalette
            VERY_PERI      -> VeryPeriPalette
            DYNAMIC        -> EmeraldNightPalette
            CUSTOM         -> ThemeManager.customPalette.value
        }
    }
}

// ─────────────────────────────────────────────────
// CUSTOM PALETTE GENERATION — Generate from a seed color
// ─────────────────────────────────────────────────
fun generatePaletteFromSeed(
    seedPrimary: Color,
    seedAccent: Color = seedPrimary,
    isDark: Boolean = true
): ThemePalette {
    // Extract base luminance to derive surface tones
    val r = seedPrimary.red
    val g = seedPrimary.green
    val b = seedPrimary.blue

    return if (isDark) {
        val bgBase = Color(
            red = (r * 0.04f).coerceIn(0f, 1f),
            green = (g * 0.04f).coerceIn(0f, 1f),
            blue = (b * 0.04f).coerceIn(0f, 1f)
        )
        val surfBase = Color(
            red = (r * 0.08f).coerceIn(0f, 1f),
            green = (g * 0.08f).coerceIn(0f, 1f),
            blue = (b * 0.08f).coerceIn(0f, 1f)
        )
        val surfVariant = Color(
            red = (r * 0.12f).coerceIn(0f, 1f),
            green = (g * 0.12f).coerceIn(0f, 1f),
            blue = (b * 0.12f).coerceIn(0f, 1f)
        )
        ThemePalette(
            background = bgBase,
            surface = surfBase,
            surfaceVariant = surfVariant,
            cardElevation1 = surfBase,
            cardElevation2 = Color(
                red = (r * 0.10f).coerceIn(0f, 1f),
                green = (g * 0.10f).coerceIn(0f, 1f),
                blue = (b * 0.10f).coerceIn(0f, 1f)
            ),
            cardElevation3 = surfVariant,
            primary = seedPrimary,
            secondary = seedPrimary.copy(alpha = 0.7f),
            accent = seedAccent,
            onPrimary = Color(0xFF000000),
            textPrimary = Color(0xFFF1F5F9),
            textSecondary = Color(0xFF8B9DB8),
            textMuted = Color(0xFF4F6378),
            glowColor = seedPrimary.copy(alpha = 0.15f),
            error = Color(0xFFFF6B6B),
            outline = surfVariant.copy(alpha = 0.6f),
            scrim = Color(0xCC000000),
            shimmer = Color(0x1AFFFFFF),
            isLight = false
        )
    } else {
        ThemePalette(
            background = Color(0xFFF4EFEA),
            surface = Color(0xFFFAF6F0),
            surfaceVariant = Color(0xFFF2ECE4),
            cardElevation1 = Color(0xFFFAF6F0),
            cardElevation2 = Color(0xFFFAF8F5),
            cardElevation3 = Color(0xFFEFE6DB),
            primary = seedPrimary,
            secondary = seedPrimary.copy(alpha = 0.7f),
            accent = seedAccent,
            onPrimary = Color(0xFFFFFFFF),
            textPrimary = Color(0xFF1A1A2E),
            textSecondary = Color(0xFF5F5A66),
            textMuted = Color(0xFF8C8594),
            glowColor = seedPrimary.copy(alpha = 0.15f),
            error = Color(0xFFB3261E),
            outline = Color(0xFFEAE3DB),
            scrim = Color(0x66000000),
            shimmer = Color(0x0A000000),
            isLight = true
        )
    }
}

fun generateCustomPalette(
    primary: Color,
    accent: Color,
    background: Color,
    isLight: Boolean
): ThemePalette {
    return if (!isLight) {
        val r = background.red
        val g = background.green
        val b = background.blue
        val surface = Color(
            red = (r * 1.5f).coerceIn(0f, 1f),
            green = (g * 1.5f).coerceIn(0f, 1f),
            blue = (b * 1.5f).coerceIn(0f, 1f)
        )
        val surfaceVariant = Color(
            red = (r * 2.0f).coerceIn(0f, 1f),
            green = (g * 2.0f).coerceIn(0f, 1f),
            blue = (b * 2.0f).coerceIn(0f, 1f)
        )
        ThemePalette(
            background = background,
            surface = surface,
            surfaceVariant = surfaceVariant,
            cardElevation1 = surface,
            cardElevation2 = Color(
                red = (r * 1.8f).coerceIn(0f, 1f),
                green = (g * 1.8f).coerceIn(0f, 1f),
                blue = (b * 1.8f).coerceIn(0f, 1f)
            ),
            cardElevation3 = surfaceVariant,
            primary = primary,
            secondary = primary.copy(alpha = 0.7f),
            accent = accent,
            onPrimary = Color(0xFF000000),
            textPrimary = Color(0xFFF1F5F9),
            textSecondary = Color(0xFF8B9DB8),
            textMuted = Color(0xFF4F6378),
            glowColor = primary.copy(alpha = 0.15f),
            error = Color(0xFFFF6B6B),
            outline = surfaceVariant.copy(alpha = 0.6f),
            scrim = Color(0xCC000000),
            shimmer = Color(0x1AFFFFFF),
            isLight = false
        )
    } else {
        val r = background.red
        val g = background.green
        val b = background.blue
        val surface = Color(
            red = (r * 0.98f).coerceIn(0f, 1f),
            green = (g * 0.98f).coerceIn(0f, 1f),
            blue = (b * 0.98f).coerceIn(0f, 1f)
        )
        val surfaceVariant = Color(
            red = (r * 0.95f).coerceIn(0f, 1f),
            green = (g * 0.95f).coerceIn(0f, 1f),
            blue = (b * 0.95f).coerceIn(0f, 1f)
        )
        ThemePalette(
            background = background,
            surface = surface,
            surfaceVariant = surfaceVariant,
            cardElevation1 = surface,
            cardElevation2 = Color(
                red = (r * 0.97f).coerceIn(0f, 1f),
                green = (g * 0.97f).coerceIn(0f, 1f),
                blue = (b * 0.97f).coerceIn(0f, 1f)
            ),
            cardElevation3 = surfaceVariant,
            primary = primary,
            secondary = primary.copy(alpha = 0.7f),
            accent = accent,
            onPrimary = Color(0xFFFFFFFF),
            textPrimary = Color(0xFF1A1A2E),
            textSecondary = Color(0xFF5F5A66),
            textMuted = Color(0xFF8C8594),
            glowColor = primary.copy(alpha = 0.15f),
            error = Color(0xFFB3261E),
            outline = Color(0xFFEAE3DB),
            scrim = Color(0x66000000),
            shimmer = Color(0x0A000000),
            isLight = true
        )
    }
}

