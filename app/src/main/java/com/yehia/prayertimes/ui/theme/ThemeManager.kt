package com.yehia.prayertimes.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object ThemeManager {

    private const val PREFS_NAME = "prayer_times_prefs"
    private const val KEY_THEME = "selected_theme"
    private const val KEY_AMOLED = "amoled_pure_black"
    private const val KEY_GRADIENTS = "enable_background_gradients"
    
    private const val KEY_CUSTOM_PRIMARY = "custom_primary"
    private const val KEY_CUSTOM_ACCENT = "custom_accent"
    private const val KEY_CUSTOM_BACKGROUND = "custom_background"
    private const val KEY_CUSTOM_IS_LIGHT = "custom_is_light"

    private val defaultCustomPalette = ThemePalette(
        background = Color(0xFF0F0B1E),
        surface = Color(0xFF191430),
        surfaceVariant = Color(0xFF231E40),
        cardElevation1 = Color(0xFF191430),
        cardElevation2 = Color(0xFF201B39),
        cardElevation3 = Color(0xFF272246),
        primary = Color(0xFFD0BCFF),
        secondary = Color(0xFFCCC2DC),
        accent = Color(0xFFEFB8C8),
        onPrimary = Color(0xFF381E72),
        textPrimary = Color(0xFFE6E1E5),
        textSecondary = Color(0xFFCAC4D0),
        textMuted = Color(0xFF938F99),
        glowColor = Color(0x26D0BCFF),
        error = Color(0xFFF2B8B5),
        outline = Color(0xFF49454F),
        scrim = Color(0xCC000000),
        shimmer = Color(0x1AFFFFFF),
        isLight = false
    )

    private val _currentTheme = MutableStateFlow(AppTheme.TIME_OF_DAY)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private val _customPalette = MutableStateFlow(defaultCustomPalette)
    val customPalette: StateFlow<ThemePalette> = _customPalette.asStateFlow()

    private val _currentPalette = MutableStateFlow(AppTheme.TIME_OF_DAY.getPalette())
    val currentPalette: StateFlow<ThemePalette> = _currentPalette.asStateFlow()

    /** Increments every minute when TIME_OF_DAY is active to force recomposition. */
    private val _timeOfDayTick = MutableStateFlow(0)
    val timeOfDayTick: StateFlow<Int> = _timeOfDayTick.asStateFlow()

    private var timeTickJob: kotlinx.coroutines.Job? = null
    private val scope = kotlinx.coroutines.CoroutineScope(
        kotlinx.coroutines.Dispatchers.Main + kotlinx.coroutines.SupervisorJob()
    )

    private var _amoledModeEnabled = false
    val amoledModeEnabled: Boolean get() = _amoledModeEnabled

    private val _gradientsEnabled = MutableStateFlow(false)
    val gradientsEnabled: StateFlow<Boolean> = _gradientsEnabled.asStateFlow()

    fun getPalette(theme: AppTheme): ThemePalette = theme.getPalette()

    fun getSwatches(theme: AppTheme): List<Color> {
        val p = theme.getPalette()
        return listOf(p.primary, p.secondary, p.accent, p.background, p.surface)
    }

    fun setTheme(context: Context, theme: AppTheme) {
        _currentTheme.value = theme
        if (theme == AppTheme.TIME_OF_DAY) {
            startTimeOfDayTick(context)
        } else {
            stopTimeOfDayTick()
        }
        updateActivePalette(context)
    }

    fun setAmoledMode(context: Context, enabled: Boolean) {
        _amoledModeEnabled = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AMOLED, enabled)
            .apply()
        updateActivePalette(context)
    }

    fun setGradientsEnabled(context: Context, enabled: Boolean) {
        _gradientsEnabled.value = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_GRADIENTS, enabled)
            .apply()
    }

    private val _targetTimeZone = MutableStateFlow<java.util.TimeZone>(java.util.TimeZone.getDefault())
    val targetTimeZone: StateFlow<java.util.TimeZone> = _targetTimeZone.asStateFlow()

    fun updateTargetTimeZone(context: Context, tz: java.util.TimeZone) {
        _targetTimeZone.value = tz
        updateActivePalette(context)
    }

    fun updateActivePalette(context: Context) {
        val theme = _currentTheme.value
        val basePalette = when {
            theme == AppTheme.TIME_OF_DAY -> TimeOfDayTheme.getPaletteForTimeZone(_targetTimeZone.value)
            theme == AppTheme.DYNAMIC     -> getDynamicPalette(context)
            theme == AppTheme.CUSTOM      -> _customPalette.value
            else                          -> theme.getPalette()
        }

        _currentPalette.value = if (_amoledModeEnabled && !basePalette.isLight) {
            basePalette.copy(
                background = Color.Black,
                surface = Color.Black,
                surfaceVariant = Color(0xFF0F0F0F),
                cardElevation1 = Color(0xFF0F0F0F),
                cardElevation2 = Color(0xFF141414),
                cardElevation3 = Color(0xFF1B1B1B)
            )
        } else {
            basePalette
        }
    }

    private fun startTimeOfDayTick(context: Context) {
        timeTickJob?.cancel()
        timeTickJob = scope.launch {
            while (true) {
                // Wait until the next minute boundary in the target location's calendar if possible, or system default
                val cal = java.util.Calendar.getInstance(_targetTimeZone.value)
                val secondsUntilNextMinute = 60 - cal.get(java.util.Calendar.SECOND)
                kotlinx.coroutines.delay(secondsUntilNextMinute * 1000L)
                if (_currentTheme.value == AppTheme.TIME_OF_DAY) {
                    _timeOfDayTick.value++
                    updateActivePalette(context)
                }
            }
        }
    }

    private fun stopTimeOfDayTick() {
        timeTickJob?.cancel()
        timeTickJob = null
    }

    fun getDynamicPalette(context: Context): ThemePalette {
        val isDark = (context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val dynamicScheme = if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            ThemePalette(
                background = dynamicScheme.background,
                surface = dynamicScheme.surface,
                surfaceVariant = dynamicScheme.surfaceVariant,
                cardElevation1 = dynamicScheme.surface,
                cardElevation2 = dynamicScheme.surfaceVariant,
                cardElevation3 = dynamicScheme.primaryContainer.copy(alpha = 0.5f),
                primary = dynamicScheme.primary,
                secondary = dynamicScheme.secondary,
                accent = dynamicScheme.tertiary,
                onPrimary = dynamicScheme.onPrimary,
                textPrimary = dynamicScheme.onBackground,
                textSecondary = dynamicScheme.onSurfaceVariant,
                textMuted = dynamicScheme.onSurfaceVariant.copy(alpha = 0.6f),
                glowColor = dynamicScheme.primary.copy(alpha = 0.15f),
                error = dynamicScheme.error,
                outline = dynamicScheme.outline,
                scrim = dynamicScheme.scrim,
                shimmer = if (isDark) Color(0x1AFFFFFF) else Color(0x0A000000),
                isLight = !isDark
            )
        } else {
            if (isDark) AppTheme.EMERALD_NIGHT.getPalette() else AppTheme.SALAM_TWILIGHT.getPalette()
        }
    }

    fun updateCustomPalette(palette: ThemePalette) {
        _customPalette.value = palette
        if (_currentTheme.value == AppTheme.CUSTOM) {
            _currentPalette.value = palette
        }
    }

    fun saveCustomPalette(context: Context, primary: Color, accent: Color, background: Color, isLight: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt(KEY_CUSTOM_PRIMARY, primary.toArgb())
            putInt(KEY_CUSTOM_ACCENT, accent.toArgb())
            putInt(KEY_CUSTOM_BACKGROUND, background.toArgb())
            putBoolean(KEY_CUSTOM_IS_LIGHT, isLight)
            apply()
        }
        val palette = generateCustomPalette(primary, accent, background, isLight)
        updateCustomPalette(palette)
        updateActivePalette(context)
    }

    fun loadCustomPalette(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.contains(KEY_CUSTOM_PRIMARY)) {
            val primary = Color(prefs.getInt(KEY_CUSTOM_PRIMARY, Color(0xFFD0BCFF).toArgb()))
            val accent = Color(prefs.getInt(KEY_CUSTOM_ACCENT, Color(0xFFEFB8C8).toArgb()))
            val background = Color(prefs.getInt(KEY_CUSTOM_BACKGROUND, Color(0xFF0F0B1E).toArgb()))
            val isLight = prefs.getBoolean(KEY_CUSTOM_IS_LIGHT, false)
            _customPalette.value = generateCustomPalette(primary, accent, background, isLight)
        }
    }

    fun saveTheme(context: Context, theme: AppTheme) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, theme.name).apply()
    }

    fun loadTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _amoledModeEnabled = prefs.getBoolean(KEY_AMOLED, false)
        _gradientsEnabled.value = prefs.getBoolean(KEY_GRADIENTS, false)
        loadCustomPalette(context)
        val themeName = prefs.getString(KEY_THEME, AppTheme.TIME_OF_DAY.name) ?: AppTheme.TIME_OF_DAY.name
        val theme = try {
            AppTheme.valueOf(themeName)
        } catch (_: IllegalArgumentException) {
            AppTheme.TIME_OF_DAY
        }
        setTheme(context, theme)
    }
}
