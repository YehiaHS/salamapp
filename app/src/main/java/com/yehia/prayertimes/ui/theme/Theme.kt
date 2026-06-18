package com.yehia.prayertimes.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.yehia.prayertimes.utils.LanguageManager

@Composable
fun PrayerTimesTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val currentTheme by ThemeManager.currentTheme.collectAsState()
    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val amoledMode = ThemeManager.amoledModeEnabled

    LaunchedEffect(currentTheme, isSystemDark, amoledMode) {
        ThemeManager.updateActivePalette(context)
    }

    val palette by ThemeManager.currentPalette.collectAsState()

    // Use lightColorScheme for light themes, darkColorScheme for dark
    val colorScheme = if (palette.isLight) {
        lightColorScheme(
            primary = palette.primary,
            secondary = palette.secondary,
            tertiary = palette.accent,
            background = palette.background,
            surface = palette.surface,
            surfaceVariant = palette.surfaceVariant,
            onPrimary = palette.onPrimary,
            onSecondary = palette.textPrimary,
            onTertiary = palette.onPrimary,
            onBackground = palette.textPrimary,
            onSurface = palette.textPrimary,
            onSurfaceVariant = palette.textSecondary,
            error = palette.error,
            onError = palette.onPrimary,
            outline = palette.outline,
            outlineVariant = palette.outline,
            scrim = palette.scrim
        )
    } else {
        darkColorScheme(
            primary = palette.primary,
            secondary = palette.secondary,
            tertiary = palette.accent,
            background = palette.background,
            surface = palette.surface,
            surfaceVariant = palette.surfaceVariant,
            onPrimary = palette.onPrimary,
            onSecondary = palette.textPrimary,
            onTertiary = palette.onPrimary,
            onBackground = palette.textPrimary,
            onSurface = palette.textPrimary,
            onSurfaceVariant = palette.textSecondary,
            error = palette.error,
            onError = palette.textPrimary,
            outline = palette.outline,
            outlineVariant = palette.outline,
            scrim = palette.scrim
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = palette.background.toArgb()
                window.navigationBarColor = palette.surface.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                // Light status bar icons for dark themes, dark icons for light themes
                insetsController.isAppearanceLightStatusBars = palette.isLight
                insetsController.isAppearanceLightNavigationBars = palette.isLight
            }
        }
    }

    val layoutDirection = if (LanguageManager.isRtl()) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
