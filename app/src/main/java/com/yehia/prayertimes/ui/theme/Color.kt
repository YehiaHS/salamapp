package com.yehia.prayertimes.ui.theme

import androidx.compose.ui.graphics.Color

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
    return if (isLight) {
        IllustrationPalette(
            skyGradientTop = primary.copy(alpha = 0.85f),
            skyGradientBottom = secondary.copy(alpha = 0.6f),
            moonColor = Color(0xFFF4EFEA),
            starColor = accent.copy(alpha = 0.8f),
            buildingColor = primary.copy(alpha = 0.25f),
            glowColor = primary.copy(alpha = 0.15f),
            badgeBg = secondary.copy(alpha = 0.12f),
            badgeText = secondary
        )
    } else {
        IllustrationPalette(
            skyGradientTop = primary.copy(alpha = 0.55f),
            skyGradientBottom = secondary.copy(alpha = 0.35f),
            moonColor = textPrimary.copy(alpha = 0.9f),
            starColor = accent.copy(alpha = 0.7f),
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
    background = Color(0xFFF4EFEA), // Warm sand background
    surface = Color(0xFFFAF6F0),    // Milky light surface
    surfaceVariant = Color(0xFFF2ECE4), // Slightly darker sand shade
    cardElevation1 = Color(0xFFFAF6F0),
    cardElevation2 = Color(0xFFFAF8F5), // Warm milky-sand card base
    cardElevation3 = Color(0xFFEFE6DB), // Active hover card
    primary = Color(0xFF3E2A75),        // Deep Twilight Purple
    secondary = Color(0xFF5C419E),      // Mid-tone Purple
    accent = Color(0xFFE8DEF8),         // Soft violet accent
    onPrimary = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF2C1E50),    // Purple-black headers
    textSecondary = Color(0xFF5F5A66),  // Charcoal
    textMuted = Color(0xFF8C8594),      // Subdued gray
    glowColor = Color(0x263E2A75),
    error = Color(0xFFB3261E),
    outline = Color(0xFFEAE3DB),        // Soft separators
    scrim = Color(0x66000000),
    shimmer = Color(0x0A000000),
    isLight = true
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

// ─────────────────────────────────────────────────
// APP THEME ENUM
// ─────────────────────────────────────────────────
enum class AppTheme(val displayName: String) {
    SALAM_TWILIGHT("Salam Twilight"),
    EMERALD_NIGHT("Emerald Night"),
    ROYAL_GOLD("Royal Gold"),
    DESERT_ROSE("Desert Rose"),
    OCEAN_BLUE("Ocean Blue"),
    MIDNIGHT_PURPLE("Midnight Purple"),
    DYNAMIC("Dynamic Wallpaper"),
    CUSTOM("Custom");

    fun getPalette(): ThemePalette {
        return when (this) {
            SALAM_TWILIGHT -> SalamTwilightPalette
            EMERALD_NIGHT -> EmeraldNightPalette
            ROYAL_GOLD -> RoyalGoldPalette
            DESERT_ROSE -> DesertRosePalette
            OCEAN_BLUE -> OceanBluePalette
            MIDNIGHT_PURPLE -> MidnightPurplePalette
            DYNAMIC -> EmeraldNightPalette
            CUSTOM -> ThemeManager.customPalette.value
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

