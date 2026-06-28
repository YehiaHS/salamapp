package com.yehia.prayertimes.ui.screens

import android.content.Context
import java.util.Locale
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.draw.alpha
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.data.PrayerCalculationResult
import com.yehia.prayertimes.data.PrayerTimeItem
import com.yehia.prayertimes.data.PrayerType
import com.yehia.prayertimes.ui.theme.SalamCard
import com.yehia.prayertimes.ui.theme.SalamIconBadge
import com.yehia.prayertimes.ui.theme.SalamScreenScaffold
import com.yehia.prayertimes.ui.theme.SalamSectionHeader
import com.yehia.prayertimes.ui.theme.SalamSpacing
import com.yehia.prayertimes.ui.theme.SalamShapes
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.theme.salamClickable
import com.yehia.prayertimes.ui.theme.staggeredEntrance
import com.yehia.prayertimes.ui.theme.toIllustrationPalette
import com.yehia.prayertimes.ui.viewmodel.PrayerViewModel
import com.yehia.prayertimes.utils.HijriCalendar
import com.yehia.prayertimes.utils.NotificationHelper
import com.yehia.prayertimes.utils.LanguageManager
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun MainScreen(
    viewModel: PrayerViewModel,
    @Suppress("UNUSED_PARAMETER") onNavigateToQibla: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onDetectLocation: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    val countdownStrState = viewModel.countdownStr.collectAsState()
    val progressState = viewModel.nextPrayerProgress.collectAsState()
    
    val lat by viewModel.latitude.collectAsState()
    val lng by viewModel.longitude.collectAsState()

    var showLocationDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var cityName by remember { mutableStateOf("") }
    LaunchedEffect(lat, lng) {
        withContext(Dispatchers.IO) {
            val fallback = "${String.format(Locale.US, "%.3f", lat)}, ${String.format(Locale.US, "%.3f", lng)}"
            val name = try {
                val geocoder = android.location.Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: address.adminArea
                    val country = address.countryName
                    if (city != null && country != null) {
                        "$city, $country"
                    } else city ?: country ?: fallback
                } else {
                    fallback
                }
            } catch (e: Exception) {
                fallback
            }
            cityName = name
        }
    }

    val currentLang = LanguageManager.currentLang.value
    val displayCityName = remember(cityName, currentLang) {
        if (currentLang == "ar") {
            cityName.replace("Egypt", "مصر")
                .replace("Cairo", "القاهرة")
                .replace("Giza", "الجيزة")
                .replace("Alexandria", "الإسكندرية")
        } else {
            cityName
        }
    }

    // Today's Hijri Date
    val timeZone by viewModel.timeZone.collectAsState()
    val todayHijri = remember(timeZone) { HijriCalendar.getTodayHijri(timeZone) }
    val hijriStr = remember(todayHijri, currentLang) { HijriCalendar.formatHijriDate(todayHijri, currentLang) }

    // Smooth entry animation for bottom items
    val itemsAlpha = remember { Animatable(0f) }
    LaunchedEffect(uiState) {
        if (uiState != null) {
            itemsAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
    }

    SalamScreenScaffold(
        showGeometricPattern = true,
        backgroundBrush = SolidColor(palette.background)
    ) {
        uiState?.let { data ->
            val currentActiveIndex = remember(data) {
                data.items.indexOfFirst { it.type == data.currentPrayer?.type }
            }
            val primaryItems = remember(data.items) {
                data.items.filter { it.type != PrayerType.SUNRISE && it.type != PrayerType.QIYAM }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = itemsAlpha.value),
                verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
            ) {
                item {
                    Spacer(modifier = Modifier.height(SalamSpacing.elementGap))
                }

                // High fidelity sand-parchment top header row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = SalamSpacing.elementGap),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Central Salam Branding aligned left
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = SalamSpacing.cardPaddingInner),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "${LanguageManager.get("greeting")} 👋",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = palette.textSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = LanguageManager.get("salam"),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = palette.textPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = if (currentLang == "ar") androidx.compose.ui.text.font.FontFamily.Default else androidx.compose.ui.text.font.FontFamily.Serif
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = LanguageManager.get("may_peace"),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = palette.textMuted
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        // Right Settings Badge
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(palette.surface, CircleShape)
                                .salamClickable { onNavigateToSettings() }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alert Settings",
                                tint = palette.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Hijri Date Banner
                item {
                    SalamCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = SalamSpacing.elementGap),
                        elevation = 1,
                        shape = SalamShapes.expressiveCorner1
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = SalamSpacing.cardPaddingInner, vertical = SalamSpacing.elementGap),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Hijri Calendar",
                                    tint = palette.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(SalamSpacing.elementGap))
                                Text(
                                    text = LanguageManager.get("hijri_date"),
                                    color = palette.textSecondary,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = hijriStr,
                                color = palette.primary,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(SalamSpacing.elementGap))
                }

                // Main Prayer Hero Card
                item {
                    ActivePrayerHero(
                        data = data,
                        countdownStrState = countdownStrState,
                        cityName = displayCityName,
                        timeZone = timeZone,
                        onLocationClick = { showLocationDialog = true }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
                }

                // Scrollable Prayer Times list header
                item {
                    SalamSectionHeader(title = LanguageManager.get("todays_prayers"))
                }

                // Prayer times list with Worship Progress Checkmarks
                itemsIndexed(primaryItems, key = { _, item -> item.type.name }) { index, item ->
                    val isFajrWithSunriseActive = item.type == PrayerType.FAJR && data.currentPrayer?.type == PrayerType.SUNRISE
                    val isIshaWithQiyamActive = item.type == PrayerType.ISHA && data.currentPrayer?.type == PrayerType.QIYAM
                    val isActive = data.currentPrayer?.type == item.type || isFajrWithSunriseActive || isIshaWithQiyamActive
                    
                    val originalIndex = data.items.indexOfFirst { it.type == item.type }
                    val isPast = currentActiveIndex >= 0 && originalIndex < currentActiveIndex

                    val nestedItem = when (item.type) {
                        PrayerType.FAJR -> data.items.firstOrNull { it.type == PrayerType.SUNRISE }
                        PrayerType.ISHA -> data.items.firstOrNull { it.type == PrayerType.QIYAM }
                        else -> null
                    }
                    
                    PrayerTimeRow(
                        item = item,
                        isActive = isActive,
                        isPast = isPast,
                        index = index,
                        nestedItem = nestedItem,
                        currentActivePrayerType = data.currentPrayer?.type
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(140.dp))
                }
            }
        }
    }

    // Location & coordinates detail popup dialog
    if (showLocationDialog) {
        val coroutineScope = rememberCoroutineScope()
        var isManualMode by remember {
            mutableStateOf(
                context.getSharedPreferences("prayer_notification_prefs", Context.MODE_PRIVATE)
                    .getBoolean("location_mode_manual", false)
            )
        }
        var manualLatInput by remember { mutableStateOf(lat.toString()) }
        var manualLngInput by remember { mutableStateOf(lng.toString()) }
        var searchQuery by remember { mutableStateOf("") }
        var searchResults by remember { mutableStateOf<List<android.location.Address>>(emptyList()) }
        var isSearching by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = {
                Text(
                    text = "Location Information",
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Mode selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isManualMode = false }
                        ) {
                            RadioButton(
                                selected = !isManualMode,
                                onClick = { isManualMode = false },
                                colors = RadioButtonDefaults.colors(selectedColor = palette.primary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Automatic (GPS)", color = palette.textPrimary, style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isManualMode = true }
                        ) {
                            RadioButton(
                                selected = isManualMode,
                                onClick = { isManualMode = true },
                                colors = RadioButtonDefaults.colors(selectedColor = palette.primary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Manual Input", color = palette.textPrimary, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    if (!isManualMode) {
                        Text(
                            text = "Salam uses your device location to compute highly accurate prayer times for your current coordinates.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary)
                        )
                        SalamCard(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 1
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Current Coordinates",
                                    style = MaterialTheme.typography.labelSmall.copy(color = palette.textMuted)
                                )
                                Text(
                                    text = String.format(Locale.US, "%.5f, %.5f", lat, lng),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = palette.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(palette.primary.copy(alpha = 0.12f), SalamShapes.pill)
                                .clickable {
                                    onDetectLocation()
                                    showLocationDialog = false
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Detect via GPS Now", color = palette.primary, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = "Search for a city name or enter decimal coordinates manually.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary)
                        )
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search location (e.g. Cairo)", color = palette.textSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                if (isSearching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = palette.primary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    IconButton(
                                        onClick = {
                                            if (searchQuery.isNotBlank()) {
                                                isSearching = true
                                                coroutineScope.launch(Dispatchers.IO) {
                                                    try {
                                                        val geocoder = android.location.Geocoder(context, Locale.getDefault())
                                                        @Suppress("DEPRECATION")
                                                        val addresses = geocoder.getFromLocationName(searchQuery, 5)
                                                        withContext(Dispatchers.Main) {
                                                            if (addresses != null && addresses.isNotEmpty()) {
                                                                searchResults = addresses
                                                            } else {
                                                                android.widget.Toast.makeText(context, "No locations found", android.widget.Toast.LENGTH_SHORT).show()
                                                            }
                                                            isSearching = false
                                                        }
                                                    } catch (e: Exception) {
                                                        withContext(Dispatchers.Main) {
                                                            android.widget.Toast.makeText(context, "Search failed: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
                                                            isSearching = false
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Search, contentDescription = "Search", tint = palette.primary)
                                    }
                                }
                            }
                        )

                        if (searchResults.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 120.dp),
                                colors = CardDefaults.cardColors(containerColor = palette.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    searchResults.forEach { address ->
                                        val name = listOfNotNull(
                                            address.locality ?: address.subAdminArea,
                                            address.adminArea,
                                            address.countryName
                                        ).joinToString(", ")
                                        
                                        Text(
                                            text = name.ifBlank { address.featureName ?: "Unknown Location" },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    manualLatInput = address.latitude.toString()
                                                    manualLngInput = address.longitude.toString()
                                                    searchResults = emptyList()
                                                    searchQuery = name.ifBlank { address.featureName ?: "" }
                                                }
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            color = palette.textPrimary,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = manualLatInput,
                            onValueChange = { manualLatInput = it },
                            label = { Text("Latitude (-90 to 90)", color = palette.textSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = manualLngInput,
                            onValueChange = { manualLngInput = it },
                            label = { Text("Longitude (-180 to 180)", color = palette.textSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(palette.primary, SalamShapes.pill)
                                .clickable {
                                    val targetLat = manualLatInput.toDoubleOrNull()
                                    val targetLng = manualLngInput.toDoubleOrNull()
                                    if (targetLat != null && targetLng != null && targetLat in -90.0..90.0 && targetLng in -180.0..180.0) {
                                        val sharedPref = context.getSharedPreferences("prayer_notification_prefs", Context.MODE_PRIVATE)
                                        sharedPref.edit()
                                            .putBoolean("location_mode_manual", true)
                                            .putString("latitude", targetLat.toString())
                                            .putString("longitude", targetLng.toString())
                                            .apply()
                                        viewModel.setLocation(targetLat, targetLng)
                                        showLocationDialog = false
                                    } else {
                                        android.widget.Toast.makeText(context, "Please enter valid coordinates", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Apply Coordinates", color = palette.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Close", color = palette.primary)
                }
            },
            containerColor = palette.cardElevation2
        )
    }
}

@Composable
fun ActivePrayerHero(
    data: PrayerCalculationResult,
    countdownStrState: State<String>,
    cityName: String,
    timeZone: java.util.TimeZone,
    onLocationClick: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()

    val context = LocalContext.current

    // Dynamic infinite breath glow for the celestial sun/moon aura
    val infiniteTransition = rememberInfiniteTransition(label = "celestialGlow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    SalamCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        elevation = 3,
        shape = SalamShapes.squircle
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            val illustrationPalette = palette.toIllustrationPalette()

            // 1. Purple gradient twilight background sky
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(illustrationPalette.skyGradientTop, illustrationPalette.skyGradientBottom)
                        )
                    )
            )

            // 2. Custom canvas rendering dynamic celestial orbit (flowing constantly)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // Twinkling stars
                        val starColor = illustrationPalette.starColor.copy(alpha = 0.35f)
                        drawCircle(starColor, 2f, Offset(size.width * 0.15f, size.height * 0.25f))
                        drawCircle(starColor, 1.5f, Offset(size.width * 0.45f, size.height * 0.15f))
                        drawCircle(starColor, 2f, Offset(size.width * 0.75f, size.height * 0.35f))
                        drawCircle(starColor, 1.5f, Offset(size.width * 0.3f, size.height * 0.5f))
                        
                        // Parse detailed time fields for precise ms calculation
                        val cal = Calendar.getInstance()
                        val currentTimeMs = cal.timeInMillis

                        val sunriseItem = data.items.firstOrNull { it.type == PrayerType.SUNRISE }
                        val maghribItem = data.items.firstOrNull { it.type == PrayerType.MAGHRIB }

                        val sunriseMs = sunriseItem?.time?.time ?: (cal.clone() as Calendar).apply {
                            set(Calendar.HOUR_OF_DAY, 6)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis

                        val maghribMs = maghribItem?.time?.time ?: (cal.clone() as Calendar).apply {
                            set(Calendar.HOUR_OF_DAY, 18)
                            set(Calendar.MINUTE, 30)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis

                        val isDaytime = currentTimeMs in sunriseMs..maghribMs

                        // Parabolic orbit calculation mapping across the entire card layout
                        val cx: Float
                        val cy: Float

                        if (isDaytime) {
                            val duration = (maghribMs - sunriseMs).toFloat()
                            val progress = ((currentTimeMs - sunriseMs).toFloat() / duration).coerceIn(0f, 1f)
                            cx = size.width * (0.08f + 0.84f * progress)
                            cy = size.height * (0.75f - 0.52f * kotlin.math.sin(progress * Math.PI).toFloat())
                            
                            // Draw glowing sun with breathing aura
                            drawCircle(
                                color = illustrationPalette.glowColor.copy(alpha = 0.15f),
                                radius = 24.dp.toPx() * glowPulse,
                                center = Offset(cx, cy)
                            )
                            drawCircle(
                                color = illustrationPalette.glowColor.copy(alpha = 0.35f),
                                radius = 16.dp.toPx() * glowPulse,
                                center = Offset(cx, cy)
                            )
                            drawCircle(
                                color = illustrationPalette.starColor,
                                radius = 10.dp.toPx(),
                                center = Offset(cx, cy)
                            )
                        } else {
                            val elapsed: Long
                            val duration: Long
                            if (currentTimeMs >= maghribMs) {
                                // Tonight (from Maghrib to tomorrow's Sunrise)
                                val tomorrowSunrise = sunriseMs + 24 * 3600 * 1000L
                                elapsed = currentTimeMs - maghribMs
                                duration = tomorrowSunrise - maghribMs
                            } else {
                                // Last night / early morning before sunrise
                                val yesterdayMaghrib = maghribMs - 24 * 3600 * 1000L
                                elapsed = currentTimeMs - yesterdayMaghrib
                                duration = sunriseMs - yesterdayMaghrib
                            }
                            val progress = (elapsed.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                            cx = size.width * (0.08f + 0.84f * progress)
                            cy = size.height * (0.75f - 0.52f * kotlin.math.sin(progress * Math.PI).toFloat())
                            
                            // Moon aura glow
                            drawCircle(
                                color = illustrationPalette.glowColor.copy(alpha = 0.12f),
                                radius = 24.dp.toPx() * glowPulse,
                                center = Offset(cx, cy)
                            )

                            // 1. Draw the dark unilluminated disk of the moon (earthshine)
                            val r = 15.dp.toPx()
                            drawCircle(
                                color = illustrationPalette.moonColor.copy(alpha = 0.15f),
                                radius = r,
                                center = Offset(cx, cy)
                            )
                            
                            // 2. Draw the dynamically calculated illuminated phase of the moon based on Hijri day
                            val hijriDay = try {
                                com.yehia.prayertimes.utils.HijriCalendar.getTodayHijri(timeZone).day
                            } catch (e: Exception) {
                                15 // Fallback to full moon
                            }

                            val isWaxing = hijriDay <= 15
                            val f = if (isWaxing) {
                                1f - 2f * (hijriDay - 1f) / 14f
                            } else {
                                -1f + 2f * (hijriDay - 15f) / 15f
                            }

                            val moonPath = Path().apply {
                                moveTo(cx, cy - r)
                                arcTo(
                                    rect = Rect(cx - r, cy - r, cx + r, cy + r),
                                    startAngleDegrees = -90f,
                                    sweepAngleDegrees = if (isWaxing) 180f else -180f,
                                    forceMoveTo = false
                                )
                                // Draw inner shadow boundary using quadratic bezier curve
                                val controlX = cx + r * f
                                quadraticBezierTo(controlX, cy, cx, cy - r)
                                close()
                            }
                            drawPath(moonPath, illustrationPalette.moonColor.copy(alpha = 0.9f))
                        }
                    }
            )

            // 3. Mosque dome watermark silhouette (low opacity and height-restricted to resolve clashes with text)
            Image(
                painter = painterResource(id = com.yehia.prayertimes.R.drawable.ic_hero_mosque),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                colorFilter = ColorFilter.tint(illustrationPalette.buildingColor.copy(alpha = 0.15f)), // Increased opacity slightly for readability, low enough to avoid text clashes
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Increased height to prevent cutting off
                    .align(Alignment.BottomCenter)
            )

            // 4. Hero text layout and progress clock
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top part: details with drop shadow for premium celestial look
                val textShadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = 0.5f),
                    offset = Offset(0f, 4f),
                    blurRadius = 8f
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = LanguageManager.get("next_prayer").uppercase(),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFFE2D6FF), // Constant light lavender/violet
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp,
                            shadow = textShadow
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = data.nextPrayer?.let { LanguageManager.get(it.type.name.lowercase()) } ?: LanguageManager.get("fajr"),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White, // Constant clean white text
                            fontWeight = FontWeight.Black,
                            fontSize = if (LanguageManager.currentLang.value == "ar") 28.sp else 42.sp,
                            fontFamily = if (LanguageManager.currentLang.value == "ar") androidx.compose.ui.text.font.FontFamily.Default else androidx.compose.ui.text.font.FontFamily.Serif,
                            shadow = textShadow
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    CountdownText(
                        countdownStrState = countdownStrState,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White, // Constant clean white text
                            fontSize = if (LanguageManager.currentLang.value == "ar") 38.sp else 54.sp,
                            shadow = textShadow
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${LanguageManager.get("now")}: ${data.currentPrayer?.let { LanguageManager.get(it.type.name.lowercase()) } ?: LanguageManager.get("isha")}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFFFFD8E4), // Constant soft rose accent
                            fontWeight = FontWeight.Bold,
                            shadow = textShadow
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom part: Location pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF221F38).copy(alpha = 0.4f), RoundedCornerShape(50))
                            .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50))
                            .clickable { onLocationClick() }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.CenterStart),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(0xFFA78BFA), // Constant light lavender
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = cityName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White, // Constant clean white text
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Chevron",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CountdownText(
    countdownStrState: State<String>,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    Text(
        text = LanguageManager.localizeNumerals(countdownStrState.value),
        style = style,
        modifier = modifier,
        textAlign = androidx.compose.ui.text.style.TextAlign.Start,
        maxLines = 1,
        softWrap = false
    )
}

@Composable
fun FriendlyRemainingText(
    countdownStrState: State<String>,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    val countdownStr = countdownStrState.value
    val currentLang = LanguageManager.currentLang.value
    val friendlyRemaining = remember(countdownStr, currentLang) {
        val parts = countdownStr.split(":")
        if (parts.size >= 3) {
            val h = parts[0].toIntOrNull() ?: 0
            val m = parts[1].toIntOrNull() ?: 0
            val formatted = when {
                h > 0 -> if (currentLang == "ar") "-${h}س ${m}د" else "-${h}h ${m}m"
                m > 0 -> if (currentLang == "ar") "-${m}د" else "-${m}m"
                else -> if (currentLang == "ar") "-1د" else "-1m"
            }
            LanguageManager.localizeNumerals(formatted)
        } else {
            LanguageManager.localizeNumerals(countdownStr)
        }
    }
    Text(
        text = friendlyRemaining,
        style = style,
        modifier = modifier
    )
}

@Composable
fun ProgressArc(
    progressState: State<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progressAnimated by animateFloatAsState(
        targetValue = progressState.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "progressSweep"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progressAnimated,
                    useCenter = false,
                    style = Stroke(
                        width = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
    )
}

data class PrayerStyleInfo(
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val badgeBgColor: Color
)

@Composable
fun getPrayerStyleInfo(type: PrayerType): PrayerStyleInfo {
    return when (type) {
        PrayerType.FAJR -> PrayerStyleInfo(
            subtitle = LanguageManager.get("sub_fajr"),
            icon = Icons.Default.WbTwilight,
            iconColor = Color(0xFFFFB74D), // Soft orange
            badgeBgColor = Color(0xFFE65100).copy(alpha = 0.15f)
        )
        PrayerType.SUNRISE -> PrayerStyleInfo(
            subtitle = LanguageManager.get("sub_sunrise"),
            icon = Icons.Default.WbTwilight,
            iconColor = Color(0xFFFFB74D), // Soft gold/orange
            badgeBgColor = Color(0xFFF57C00).copy(alpha = 0.15f)
        )
        PrayerType.DHUHR -> PrayerStyleInfo(
            subtitle = LanguageManager.get("sub_dhuhr"),
            icon = Icons.Default.WbSunny,
            iconColor = Color(0xFFFFD54F), // Golden yellow
            badgeBgColor = Color(0xFFFBC02D).copy(alpha = 0.15f)
        )
        PrayerType.ASR -> PrayerStyleInfo(
            subtitle = LanguageManager.get("sub_asr"),
            icon = Icons.Default.WbSunny,
            iconColor = Color(0xFFFF8A65), // Warm orange
            badgeBgColor = Color(0xFFE64A19).copy(alpha = 0.15f)
        )
        PrayerType.MAGHRIB -> PrayerStyleInfo(
            subtitle = LanguageManager.get("sub_maghrib"),
            icon = Icons.Default.WbTwilight,
            iconColor = Color(0xFFE57373), // Red/rose
            badgeBgColor = Color(0xFFD32F2F).copy(alpha = 0.15f)
        )
        PrayerType.ISHA -> PrayerStyleInfo(
            subtitle = LanguageManager.get("sub_isha"),
            icon = Icons.Default.NightsStay,
            iconColor = Color(0xFFB39DDB), // Light purple
            badgeBgColor = Color(0xFF512DA8).copy(alpha = 0.15f)
        )
        PrayerType.QIYAM -> PrayerStyleInfo(
            subtitle = LanguageManager.get("sub_qiyam"),
            icon = Icons.Default.NightsStay,
            iconColor = Color(0xFF81D4FA), // Cool light blue
            badgeBgColor = Color(0xFF0288D1).copy(alpha = 0.15f)
        )
    }
}

fun getSunnahKey(type: PrayerType): String? {
    return when (type) {
        PrayerType.FAJR -> "sunnah_fajr"
        PrayerType.DHUHR -> "sunnah_dhuhr"
        PrayerType.ASR -> "sunnah_asr"
        PrayerType.MAGHRIB -> "sunnah_maghrib"
        PrayerType.ISHA -> "sunnah_isha"
        else -> null
    }
}

@Composable
fun PrayerTimeRow(
    item: PrayerTimeItem,
    isActive: Boolean,
    isPast: Boolean,
    index: Int,
    nestedItem: PrayerTimeItem? = null,
    currentActivePrayerType: PrayerType? = null
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val styleInfo = getPrayerStyleInfo(item.type)

    // Dynamic scale spring anim for highlight
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.02f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "activeScale"
    )

    // Enforce constant rounded corner shape matching mockup
    val shape = RoundedCornerShape(16.dp)
    var isExpanded by remember { mutableStateOf(false) }

    val sunnahKey = getSunnahKey(item.type)

    // Fajr expands for Sunrise, Isha expands for Qiyam
    val hasNestedItem = nestedItem != null
    val clickAction = if (sunnahKey != null || hasNestedItem) {
        { isExpanded = !isExpanded }
    } else {
        null
    }

    SalamCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .staggeredEntrance(index)
            .scale(scale),
        elevation = if (isActive) 3 else 2,
        isActive = isActive,
        shape = shape,
        onClick = clickAction
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Circular icon badge matching the mockup
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(palette.background.copy(alpha = 0.5f), CircleShape)
                            .border(1.dp, palette.outline.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = styleInfo.icon,
                            contentDescription = null,
                            tint = styleInfo.iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Stacked name/description labels
                    Column {
                        Text(
                            text = LanguageManager.get(item.type.name.lowercase()).replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        Text(
                            text = styleInfo.subtitle,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = palette.textSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Bold time text (colored palette.primary if active, else palette.textPrimary)
                    Text(
                        text = LanguageManager.formatDisplayTime(item.formattedTime),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) palette.primary else palette.textPrimary,
                            fontSize = 15.sp
                        )
                    )

                    val context = androidx.compose.ui.platform.LocalContext.current
                    val mode = NotificationHelper.getPrayerNotificationMode(context, item.type)
                    val isOff = mode == "OFF"

                    if (sunnahKey != null || hasNestedItem) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand/Collapse details",
                            tint = palette.textMuted.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Right checkmark or active alerts bell circular badge
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(
                                color = if (isActive) palette.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = if (isActive) palette.primary else palette.outline,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPast) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = if (isActive) palette.onPrimary else palette.primary,
                                modifier = Modifier
                                    .size(14.dp)
                                    .align(Alignment.Center)
                            )
                        } else {
                            Icon(
                                imageVector = if (isOff) {
                                    Icons.Default.NotificationsOff
                                } else {
                                    if (isActive) Icons.Default.NotificationsActive else Icons.Default.Notifications
                                },
                                contentDescription = if (isOff) "Muted Alert" else "Active Alert",
                                tint = if (isActive) palette.onPrimary else palette.textMuted.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(14.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isExpanded,
                enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val description = when (item.type) {
                        PrayerType.FAJR -> LanguageManager.get("sunnah_fajr")
                        PrayerType.DHUHR -> LanguageManager.get("sunnah_dhuhr")
                        PrayerType.ASR -> LanguageManager.get("sunnah_asr")
                        PrayerType.MAGHRIB -> LanguageManager.get("sunnah_maghrib")
                        PrayerType.ISHA -> LanguageManager.get("sunnah_isha")
                        else -> ""
                    }
                    if (description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = palette.primary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(start = 52.dp)
                        )
                    }

                    if (nestedItem != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        androidx.compose.material3.HorizontalDivider(
                            color = palette.outline.copy(alpha = 0.4f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(start = 52.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val nestedStyleInfo = getPrayerStyleInfo(nestedItem.type)
                        val isNestedActive = currentActivePrayerType == nestedItem.type

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 52.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(palette.background.copy(alpha = 0.3f), CircleShape)
                                        .border(1.dp, palette.outline.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = nestedStyleInfo.icon,
                                        contentDescription = null,
                                        tint = nestedStyleInfo.iconColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = LanguageManager.get(nestedItem.type.name.lowercase()).replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                        },
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textPrimary,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Text(
                                        text = nestedStyleInfo.subtitle,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = palette.textSecondary,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = LanguageManager.formatDisplayTime(nestedItem.formattedTime),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isNestedActive) palette.primary else palette.textPrimary,
                                        fontSize = 13.sp
                                    )
                                )

                                val context = androidx.compose.ui.platform.LocalContext.current
                                var nestedMode by remember(nestedItem.type) {
                                    mutableStateOf(NotificationHelper.getPrayerNotificationMode(context, nestedItem.type))
                                }
                                val isNestedOff = nestedMode == "OFF"

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = if (isNestedActive) palette.primary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isNestedActive) palette.primary else palette.outline,
                                            shape = CircleShape
                                        )
                                        .salamClickable {
                                            val newMode = if (isNestedOff) "ATHAN" else "OFF"
                                            NotificationHelper.savePrayerNotificationMode(context, nestedItem.type, newMode)
                                            NotificationHelper.schedulePrayerAlarms(context)
                                            nestedMode = newMode
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isNestedOff) Icons.Default.NotificationsOff else {
                                            if (isNestedActive) Icons.Default.NotificationsActive else Icons.Default.Notifications
                                        },
                                        contentDescription = "Nested Notification Option",
                                        tint = if (isNestedActive) palette.onPrimary else palette.textMuted.copy(alpha = 0.6f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
