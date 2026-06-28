package com.yehia.prayertimes.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

/**
 * Adaptive scale system for SalamApp.
 *
 * Provides responsive layout scaling that adapts across:
 * - Tiny budget phones (280-319dp)       — very compact, e.g. low-end Galaxy A series
 * - Small budget phones (320-359dp)       — compact, e.g. small Xiaomi/Realme phones
 * - Standard phones (360-413dp)           — baseline (360dp = common Android baseline)
 * - Large phones (414-479dp)              — compact-large, e.g. iPhone Pro Max
 * - Pre-tablet / large phones (480-599dp) — medium
 * - Tablets (600dp+)                      — expanded
 *
 * PPI/density: Android dp is already DPI-normalized. This system handles
 * *logical* screen size differences between small-dp budget phones and large flagships.
 */
data class AdaptiveScale(
    /** Multiplier for spacing/padding values (0.75 – 1.25) */
    val spacing: Float,
    /** Multiplier for icon and element sizes (0.80 – 1.20) */
    val element: Float,
    /** Multiplier for text sizes (0.84 – 1.14) */
    val text: Float,
    /** Corner radius scale (0.88 – 1.12) */
    val corners: Float,
    /** Whether we are on a tiny budget phone (< 320dp) */
    val isTiny: Boolean,
    /** Whether we are on a small screen (320–359dp) */
    val isSmall: Boolean,
    /** Whether we are on a compact (small flagship) screen (< 380dp) */
    val isCompact: Boolean,
    /** Whether we are on an expanded (tablet) screen (≥ 600dp) */
    val isExpanded: Boolean,
    /** Raw screen width in dp */
    val screenWidthDp: Int,
    /** Raw screen height in dp */
    val screenHeightDp: Int
) {
    /** Scale a Dp value by the spacing factor */
    fun Dp.scaled(): Dp = (this.value * spacing).dp
    /** Scale a Dp value by the element factor */
    fun Dp.scaledElement(): Dp = (this.value * element).dp
    /** Scale an sp value by the text factor */
    fun TextUnit.scaledText(): TextUnit = (this.value * text).sp
    /** Scale a raw float dp value */
    fun scaleDp(value: Float): Dp = (value * spacing).dp
    /** Scale a raw sp value */
    fun scaleSp(value: Float): TextUnit = (value * text).sp
}

val LocalAdaptiveScale: androidx.compose.runtime.ProvidableCompositionLocal<AdaptiveScale> = compositionLocalOf {
    // Default: 360dp baseline (common budget-to-mid Android default)
    AdaptiveScale(
        spacing = 1f,
        element = 1f,
        text = 1f,
        corners = 1f,
        isTiny = false,
        isSmall = false,
        isCompact = false,
        isExpanded = false,
        screenWidthDp = 360,
        screenHeightDp = 800
    )
}

/**
 * Computes the adaptive scale from screen dimensions.
 *
 * Width breakpoints (dp):
 *   < 280  → clamp to minimum (very old/small device)
 *   280–319 → tiny budget phone  (e.g. small Galaxy A-series)
 *   320–359 → small budget phone (e.g. Moto G compact, Xiaomi 12 mini)
 *   360–413 → standard           (baseline = 1.0, most Android phones target 360dp)
 *   414–479 → compact-large      (e.g. iPhone 14 Plus, Samsung S24+)
 *   480–599 → medium pre-tablet
 *   600+    → expanded tablet
 */
@Composable
fun rememberAdaptiveScale(): AdaptiveScale {
    val config = LocalConfiguration.current
    val screenWidthDp = config.screenWidthDp
    val screenHeightDp = config.screenHeightDp

    return remember(screenWidthDp, screenHeightDp) {
        // Baseline is 360dp wide (most common Android logical width)
        val baseWidth = 360f
        val rawRatio = screenWidthDp / baseWidth

        // Budget phones: extra-aggressive clamps to keep UI usable on tiny screens
        val spacingRatio = clampScale(rawRatio, min = 0.74f, max = 1.25f)
        val elementRatio = clampScale(rawRatio, min = 0.78f, max = 1.20f)
        // Text is intentionally less aggressively scaled — readability over space savings
        val textRatio    = clampScale(rawRatio, min = 0.82f, max = 1.14f)
        val cornerRatio  = clampScale(rawRatio, min = 0.86f, max = 1.12f)

        AdaptiveScale(
            spacing = spacingRatio,
            element = elementRatio,
            text = textRatio,
            corners = cornerRatio,
            isTiny = screenWidthDp < 320,
            isSmall = screenWidthDp in 320..359,
            isCompact = screenWidthDp < 380,
            isExpanded = screenWidthDp >= 600,
            screenWidthDp = screenWidthDp,
            screenHeightDp = screenHeightDp
        )
    }
}

private fun clampScale(raw: Float, min: Float, max: Float): Float =
    max(min, min(max, raw))

// ─────────────────────────────────────────────────
// ADAPTIVE SPACING — reads from LocalAdaptiveScale
// ─────────────────────────────────────────────────
/**
 * Adaptive spacing object. Use inside @Composable contexts.
 * Values are scaled from a 360dp baseline (common Android standard).
 */
object AdaptiveSpacing {

    // Horizontal outer padding — 20dp baseline (tighter on budget phones)
    val screenPaddingH: Dp
        @Composable get() {
            val scale = LocalAdaptiveScale.current
            // Budget phones get slightly less horizontal padding to maximise content area
            val base = if (scale.isTiny) 14f else if (scale.isSmall) 16f else 20f
            return adaptDp(base, scale.spacing)
        }

    // Vertical outer padding top — 16dp baseline
    val screenPaddingV: Dp
        @Composable get() {
            val scale = LocalAdaptiveScale.current
            val base = if (scale.isTiny) 12f else 16f
            return adaptDp(base, scale.spacing)
        }

    // Inner card padding — 14dp baseline (12dp on tiny)
    val cardPaddingInner: Dp
        @Composable get() {
            val scale = LocalAdaptiveScale.current
            val base = if (scale.isTiny) 10f else if (scale.isSmall) 12f else 14f
            return adaptDp(base, scale.spacing)
        }

    // Large inner card padding — 18dp baseline
    val cardPaddingInnerLarge: Dp
        @Composable get() {
            val scale = LocalAdaptiveScale.current
            val base = if (scale.isTiny) 14f else 18f
            return adaptDp(base, scale.spacing)
        }

    // Gap between cards — 10dp baseline
    val cardGap: Dp
        @Composable get() {
            val scale = LocalAdaptiveScale.current
            val base = if (scale.isTiny) 8f else 10f
            return adaptDp(base, scale.spacing)
        }

    // Gap between sections — 20dp baseline
    val sectionGap: Dp
        @Composable get() = adaptDp(20f, LocalAdaptiveScale.current.spacing)

    // Gap between small elements — 6dp baseline
    val elementGap: Dp
        @Composable get() {
            val scale = LocalAdaptiveScale.current
            val base = if (scale.isTiny) 4f else 6f
            return adaptDp(base, scale.spacing)
        }

    // Standard icon size — 20dp baseline
    val iconSize: Dp
        @Composable get() = adaptDp(20f, LocalAdaptiveScale.current.element)

    // Large icon size — 26dp baseline
    val iconSizeLarge: Dp
        @Composable get() = adaptDp(26f, LocalAdaptiveScale.current.element)

    // Icon circle background size — 40dp baseline
    val iconBackgroundSize: Dp
        @Composable get() = adaptDp(40f, LocalAdaptiveScale.current.element)

    // Minimum touch target — always at least 44dp regardless of screen size (accessibility)
    val touchTarget: Dp
        @Composable get() = maxOf(adaptDp(48f, LocalAdaptiveScale.current.element), 44.dp)

    private fun adaptDp(base: Float, scale: Float): Dp = (base * scale).dp
}

// ─────────────────────────────────────────────────
// ADAPTIVE TYPOGRAPHY SCALE
// ─────────────────────────────────────────────────
/**
 * Returns an adapted copy of the app Typography scaled for this screen.
 * Call inside PrayerTimesTheme to replace the static Typography with adaptive values.
 */
@Composable
fun adaptedTypography(): androidx.compose.material3.Typography {
    val scale = LocalAdaptiveScale.current
    val t = scale.text  // text scale factor

    fun Float.sp() = (this * t).sp

    return androidx.compose.material3.Typography(
        // Display tier
        displayLarge = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
            fontSize = 48f.sp(),
            lineHeight = 56f.sp(),
            letterSpacing = (-1.0).sp
        ),
        displayMedium = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
            fontSize = 36f.sp(),
            lineHeight = 44f.sp(),
            letterSpacing = (-0.8).sp
        ),
        displaySmall = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
            fontSize = 30f.sp(),
            lineHeight = 38f.sp(),
            letterSpacing = (-0.6).sp
        ),
        // Headline tier
        headlineLarge = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
            fontSize = 28f.sp(),
            lineHeight = 36f.sp(),
            letterSpacing = (-0.5).sp
        ),
        headlineMedium = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
            fontSize = 24f.sp(),
            lineHeight = 32f.sp(),
            letterSpacing = (-0.4).sp
        ),
        headlineSmall = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
            fontSize = 20f.sp(),
            lineHeight = 28f.sp(),
            letterSpacing = (-0.3).sp
        ),
        // Title tier
        titleLarge = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 18f.sp(),
            lineHeight = 26f.sp(),
            letterSpacing = (-0.2).sp
        ),
        titleMedium = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 16f.sp(),
            lineHeight = 24f.sp(),
            letterSpacing = (-0.15).sp
        ),
        titleSmall = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            fontSize = 14f.sp(),
            lineHeight = 20f.sp(),
            letterSpacing = (-0.1).sp
        ),
        // Body tier
        bodyLarge = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            fontSize = 16f.sp(),
            lineHeight = 24f.sp(),
            letterSpacing = (-0.1).sp
        ),
        bodyMedium = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            fontSize = 14f.sp(),
            lineHeight = 20f.sp(),
            letterSpacing = (-0.05).sp
        ),
        bodySmall = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            fontSize = 12f.sp(),
            lineHeight = 16f.sp(),
            letterSpacing = (-0.02).sp
        ),
        // Label tier
        labelLarge = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 14f.sp(),
            lineHeight = 20f.sp(),
            letterSpacing = (-0.05).sp
        ),
        labelMedium = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            fontSize = 12f.sp(),
            lineHeight = 16f.sp(),
            letterSpacing = (-0.02).sp
        ),
        labelSmall = androidx.compose.ui.text.TextStyle(
            fontFamily = HelveticaFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            fontSize = 10f.sp(),
            lineHeight = 14f.sp(),
            letterSpacing = 0.sp
        )
    )
}
