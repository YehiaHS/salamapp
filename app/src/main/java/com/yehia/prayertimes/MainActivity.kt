package com.yehia.prayertimes

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.TouchApp

import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.*
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yehia.prayertimes.ui.screens.*
import com.yehia.prayertimes.ui.theme.PrayerTimesTheme
import com.yehia.prayertimes.ui.theme.SalamShapes
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.viewmodel.PrayerViewModel
import com.yehia.prayertimes.ui.viewmodel.QuranViewModel
import com.yehia.prayertimes.utils.LocationHelper
import com.yehia.prayertimes.utils.NotificationHelper
import com.yehia.prayertimes.utils.LanguageManager
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp


enum class AppTab {
    PRAYER, QURAN, DHIKR, DUAS, MORE
}

enum class MoreSubScreen {
    NONE, QIBLA, HADITH, HIJRI, SETTINGS, TRACKER, NAMES, HAJJ_UMRAH, ZAKAT, ABOUT_DEVELOPER, LOCATOR, CUSTOM_THEME
}

class MainActivity : ComponentActivity() {

    private val prayerViewModel: PrayerViewModel by viewModels()

    // Permissions requesting launcher (location + notifications)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            triggerLocationFetch()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request high refresh rate (120Hz) if supported by hardware
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                display?.supportedModes?.maxByOrNull { it.refreshRate }?.let { bestMode ->
                    val params = window.attributes
                    params.preferredDisplayModeId = bestMode.modeId
                    window.attributes = params
                }
            } catch (e: Exception) {
                val params = window.attributes
                params.preferredRefreshRate = 120f
                window.attributes = params
            }
        } else {
            val params = window.attributes
            params.preferredRefreshRate = 120f
            window.attributes = params
        }

        // Initialize Notification Channels
        NotificationHelper.createNotificationChannels(this)

        // Load persisted app theme
        ThemeManager.loadTheme(this)

        // Request permissions on start
        requestPermissions()

        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            PrayerTimesTheme {
                val quranViewModel: QuranViewModel = viewModel()
                val palette by ThemeManager.currentPalette.collectAsState()
                val currentLang by LanguageManager.currentLang
                LaunchedEffect(currentLang) {
                    LanguageManager.loadLanguage(context)
                }


                val calculationMethod by prayerViewModel.calculationMethod.collectAsState()
                val madhab by prayerViewModel.madhab.collectAsState()
                val latitude by prayerViewModel.latitude.collectAsState()
                val longitude by prayerViewModel.longitude.collectAsState()
                val timeZone by prayerViewModel.timeZone.collectAsState()

                // Update active palette when location or timezone changes (specifically for TIME_OF_DAY theme)
                LaunchedEffect(timeZone) {
                    ThemeManager.updateTargetTimeZone(context, timeZone)
                }

                // Reschedule alarms dynamically when prayer parameters change
                LaunchedEffect(calculationMethod, madhab, latitude, longitude) {
                    NotificationHelper.savePrayerSettings(
                        context,
                        latitude,
                        longitude,
                        calculationMethod,
                        madhab
                    )
                    NotificationHelper.schedulePrayerAlarms(context)
                }

                var currentTab by remember { mutableStateOf(AppTab.PRAYER) }
                var activeSubScreen by remember { mutableStateOf(MoreSubScreen.NONE) }

                val selectedQuranSurah by quranViewModel.selectedSurah.collectAsState()

                // Hide bottom navigation bar when viewing Quran Details, Settings, or Qibla
                val showBottomBar = selectedQuranSurah == null && activeSubScreen == MoreSubScreen.NONE

                var showSplashScreen by remember { mutableStateOf(true) }
                val isQuranLoading by quranViewModel.isLoading.collectAsState()

                BackHandler(enabled = activeSubScreen != MoreSubScreen.NONE || selectedQuranSurah != null || currentTab != AppTab.PRAYER) {
                    if (activeSubScreen != MoreSubScreen.NONE) {
                        if (activeSubScreen == MoreSubScreen.CUSTOM_THEME) {
                            activeSubScreen = MoreSubScreen.SETTINGS
                        } else {
                            activeSubScreen = MoreSubScreen.NONE
                        }
                    } else if (selectedQuranSurah != null) {
                        quranViewModel.clearSelection()
                    } else if (currentTab != AppTab.PRAYER) {
                        currentTab = AppTab.PRAYER
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                                    .navigationBarsPadding()
                                    .height(80.dp)
                                    .shadow(
                                        elevation = 12.dp,
                                        shape = SalamShapes.expressiveCorner2
                                    )
                                    .clip(SalamShapes.expressiveCorner2)
                                    .drawBehind {
                                        drawCircle(
                                            color = palette.primary.copy(alpha = if (palette.isLight) 0.08f else 0.14f),
                                            radius = size.width * 0.35f,
                                            center = Offset(size.width * 0.22f, 0f)
                                        )
                                        drawCircle(
                                            color = palette.accent.copy(alpha = if (palette.isLight) 0.06f else 0.1f),
                                            radius = size.width * 0.28f,
                                            center = Offset(size.width * 0.82f, size.height)
                                        )
                                    }
                                    .border(
                                        width = 1.dp,
                                        color = palette.outline.copy(alpha = if (palette.isLight) 0.45f else 0.35f),
                                        shape = SalamShapes.expressiveCorner2
                                    ),
                                containerColor = palette.surface.copy(alpha = if (palette.isLight) 0.94f else 0.9f),
                                tonalElevation = 0.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == AppTab.PRAYER,
                                    onClick = { currentTab = AppTab.PRAYER },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Prayers") },
                                    label = { Text(LanguageManager.get("tab_prayers")) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = palette.primary,
                                        selectedTextColor = palette.primary,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = if (palette.isLight) palette.textSecondary else palette.textMuted,
                                        unselectedTextColor = if (palette.isLight) palette.textSecondary else palette.textMuted
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentTab == AppTab.QURAN,
                                    onClick = { currentTab = AppTab.QURAN },
                                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Quran") },
                                    label = { Text(LanguageManager.get("tab_quran")) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = palette.primary,
                                        selectedTextColor = palette.primary,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = if (palette.isLight) palette.textSecondary else palette.textMuted,
                                        unselectedTextColor = if (palette.isLight) palette.textSecondary else palette.textMuted
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentTab == AppTab.DHIKR,
                                    onClick = { currentTab = AppTab.DHIKR },
                                    icon = { Icon(painter = painterResource(id = com.yehia.prayertimes.R.drawable.ic_tasbih), contentDescription = "Dhikr", modifier = Modifier.size(24.dp)) },
                                    label = { Text(LanguageManager.get("tab_dhikr")) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = palette.primary,
                                        selectedTextColor = palette.primary,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = if (palette.isLight) palette.textSecondary else palette.textMuted,
                                        unselectedTextColor = if (palette.isLight) palette.textSecondary else palette.textMuted
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentTab == AppTab.DUAS,
                                    onClick = { currentTab = AppTab.DUAS },
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Duas") },
                                    label = { Text(LanguageManager.get("tab_duas")) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = palette.primary,
                                        selectedTextColor = palette.primary,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = if (palette.isLight) palette.textSecondary else palette.textMuted,
                                        unselectedTextColor = if (palette.isLight) palette.textSecondary else palette.textMuted
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentTab == AppTab.MORE,
                                    onClick = {
                                        currentTab = AppTab.MORE
                                        activeSubScreen = MoreSubScreen.NONE
                                    },
                                    icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More") },
                                    label = { Text(LanguageManager.get("tab_more")) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = palette.primary,
                                        selectedTextColor = palette.primary,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = if (palette.isLight) palette.textSecondary else palette.textMuted,
                                        unselectedTextColor = if (palette.isLight) palette.textSecondary else palette.textMuted
                                    )
                                )
                            }
                        }
                    },
                    containerColor = palette.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = innerPadding.calculateTopPadding(),
                                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                            )
                    ) {
                        when (currentTab) {
                            AppTab.PRAYER -> {
                                 MainScreen(
                                     viewModel = prayerViewModel,
                                     onNavigateToQibla = {
                                         currentTab = AppTab.MORE
                                         activeSubScreen = MoreSubScreen.QIBLA
                                     },
                                     onNavigateToSettings = {
                                         currentTab = AppTab.MORE
                                         activeSubScreen = MoreSubScreen.SETTINGS
                                     },
                                     onDetectLocation = {
                                         val sharedPref = getSharedPreferences("prayer_notification_prefs", Context.MODE_PRIVATE)
                                         sharedPref.edit().putBoolean("location_mode_manual", false).apply()
                                         triggerLocationFetch()
                                     }
                                 )
                            }
                            AppTab.QURAN -> {
                                if (selectedQuranSurah != null) {
                                    QuranDetailScreen(viewModel = quranViewModel)
                                } else {
                                    QuranListScreen(viewModel = quranViewModel)
                                }
                            }
                            AppTab.DHIKR -> {
                                DhikrScreen()
                            }
                            AppTab.DUAS -> {
                                DuasScreen()
                            }
                            AppTab.MORE -> {
                                when (activeSubScreen) {
                                    MoreSubScreen.NONE -> {
                                        MoreScreen(
                                            onNavigateToQibla = { activeSubScreen = MoreSubScreen.QIBLA },
                                            onNavigateToHadith = { activeSubScreen = MoreSubScreen.HADITH },
                                            onNavigateToHijri = { activeSubScreen = MoreSubScreen.HIJRI },
                                            onNavigateToSettings = { activeSubScreen = MoreSubScreen.SETTINGS },
                                            onNavigateToTracker = { activeSubScreen = MoreSubScreen.TRACKER },
                                            onNavigateToNames = { activeSubScreen = MoreSubScreen.NAMES },
                                            onNavigateToHajj = { activeSubScreen = MoreSubScreen.HAJJ_UMRAH },
                                            onNavigateToZakat = { activeSubScreen = MoreSubScreen.ZAKAT },
                                            onNavigateToAboutDeveloper = { activeSubScreen = MoreSubScreen.ABOUT_DEVELOPER },
                                            onNavigateToLocator = { activeSubScreen = MoreSubScreen.LOCATOR }
                                        )
                                    }
                                    MoreSubScreen.QIBLA -> {
                                        QiblaScreen(
                                            viewModel = prayerViewModel,
                                            onNavigateBack = { activeSubScreen = MoreSubScreen.NONE }
                                        )
                                    }
                                    MoreSubScreen.HADITH -> {
                                        HadithScreen(
                                            onNavigateBack = { activeSubScreen = MoreSubScreen.NONE }
                                        )
                                    }
                                    MoreSubScreen.HIJRI -> {
                                        HijriCalendarScreen(
                                            viewModel = prayerViewModel,
                                            onNavigateBack = { activeSubScreen = MoreSubScreen.NONE }
                                        )
                                    }
                                    MoreSubScreen.SETTINGS -> {
                                        SettingsScreen(
                                            viewModel = prayerViewModel,
                                            onNavigateBack = { activeSubScreen = MoreSubScreen.NONE },
                                            onNavigateToCustomTheme = { activeSubScreen = MoreSubScreen.CUSTOM_THEME }
                                        )
                                    }
                                    MoreSubScreen.CUSTOM_THEME -> {
                                        CustomThemeScreen(
                                            onNavigateBack = { activeSubScreen = MoreSubScreen.SETTINGS }
                                        )
                                    }
                                    MoreSubScreen.TRACKER -> {
                                        TrackerScreen(
                                            onNavigateBack = { activeSubScreen = MoreSubScreen.NONE }
                                        )
                                    }
                                    MoreSubScreen.NAMES -> {
                                        NamesScreen(
                                            onNavigateBack = { activeSubScreen = MoreSubScreen.NONE }
                                        )
                                    }
                                    MoreSubScreen.HAJJ_UMRAH -> {
                                        HajjUmrahScreen(
                                            onNavigateBack = { activeSubScreen = MoreSubScreen.NONE }
                                        )
                                    }
                                    MoreSubScreen.ZAKAT -> {
                                        ZakatScreen(
                                            onNavigateBack = { activeSubScreen = MoreSubScreen.NONE }
                                        )
                                    }
                                    MoreSubScreen.ABOUT_DEVELOPER -> {
                                         AboutDeveloperScreen(
                                             onNavigateBack = { activeSubScreen = MoreSubScreen.NONE }
                                         )
                                     }
                                    MoreSubScreen.LOCATOR -> {
                                        MosqueHalalScreen(
                                            viewModel = prayerViewModel,
                                            onNavigateBack = { activeSubScreen = MoreSubScreen.NONE }
                                        )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                        visible = showSplashScreen,
                        enter = fadeIn(),
                        exit = fadeOut(animationSpec = tween(durationMillis = 600))
                    ) {
                        SplashScreen(
                            isLoading = isQuranLoading,
                            onFinished = { showSplashScreen = false }
                        )
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        requestPermissionLauncher.launch(permissions.toTypedArray())
    }

    private fun triggerLocationFetch() {
        val sharedPref = getSharedPreferences("prayer_notification_prefs", Context.MODE_PRIVATE)
        val isManual = sharedPref.getBoolean("location_mode_manual", false)
        if (isManual) {
            val lat = NotificationHelper.getSavedLatitude(this)
            val lng = NotificationHelper.getSavedLongitude(this)
            prayerViewModel.setLocation(lat, lng)
            return
        }
        LocationHelper.getCurrentLocation(this) { lat, lng ->
            prayerViewModel.setLocation(lat, lng)
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-trigger location updates on app focus
        triggerLocationFetch()
    }
}
