package com.yehia.prayertimes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Madhab
import com.yehia.prayertimes.data.PrayerType
import com.yehia.prayertimes.ui.theme.AppTheme
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.viewmodel.PrayerViewModel
import com.yehia.prayertimes.utils.NotificationHelper
import com.yehia.prayertimes.utils.LanguageManager
import android.content.Context
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import com.yehia.prayertimes.ui.theme.SalamCard
import com.yehia.prayertimes.ui.theme.SalamTopBar
import com.yehia.prayertimes.ui.theme.SalamScreenScaffold
import com.yehia.prayertimes.ui.theme.SalamSectionHeader
import com.yehia.prayertimes.ui.theme.SalamSpacing
import com.yehia.prayertimes.ui.theme.SalamShapes
import com.yehia.prayertimes.ui.theme.staggeredEntrance
import com.yehia.prayertimes.ui.theme.salamClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PrayerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCustomTheme: () -> Unit
) {
    val palette = ThemeManager.currentPalette.collectAsState().value
    val currentTheme by ThemeManager.currentTheme.collectAsState()
    val method by viewModel.calculationMethod.collectAsState()
    val madhab by viewModel.madhab.collectAsState()
    val context = LocalContext.current
    val currentThemeLang by LanguageManager.currentLang
    val prefs = remember { context.getSharedPreferences("salam_prefs", Context.MODE_PRIVATE) }
    var selectedMuezzin by remember { mutableStateOf(prefs.getString("athan_muezzin", "mishary") ?: "mishary") }
    val calculationMethods = listOf(
        CalculationMethod.EGYPTIAN to "Egyptian General Authority of Survey",
        CalculationMethod.MUSLIM_WORLD_LEAGUE to "Muslim World League",
        CalculationMethod.UMM_AL_QURA to "Umm al-Qura University",
        CalculationMethod.NORTH_AMERICA to "Islamic Society of North America (ISNA)",
        CalculationMethod.KARACHI to "University of Islamic Sciences, Karachi"
    )

    val madhabs = listOf(
        Madhab.SHAFI to "Standard (Shafi'i, Maliki, Hanbali)",
        Madhab.HANAFI to "Hanafi (Later Asr shadow length)"
    )

    val themes = AppTheme.entries.toList()

    SalamScreenScaffold {
        SalamTopBar(
            title = LanguageManager.get("title_preferences"),
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(SalamSpacing.cardGap))

            // ── Theme Selection ──────────────────────────────────────
            SalamSectionHeader(title = LanguageManager.get("settings_app_theme"))

            // Render theme cards in rows of 2 to avoid nested scrolling performance bugs
            val chunkedThemes = remember(themes) { themes.chunked(2) }
            chunkedThemes.forEachIndexed { rowIndex, rowThemes ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = SalamSpacing.cardGap),
                    horizontalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
                ) {
                    rowThemes.forEachIndexed { colIndex, theme ->
                        val isSelected = currentTheme == theme
                        val swatches = if (theme == AppTheme.DYNAMIC) {
                            val dp = ThemeManager.getDynamicPalette(context)
                            listOf(dp.primary, dp.secondary, dp.accent, dp.background, dp.surface)
                        } else {
                            ThemeManager.getSwatches(theme)
                        }
                        val themePalette = if (theme == AppTheme.DYNAMIC) {
                            ThemeManager.getDynamicPalette(context)
                        } else {
                            ThemeManager.getPalette(theme)
                        }
                        val index = rowIndex * 2 + colIndex

                        SalamCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(150.dp)
                                .staggeredEntrance(index),
                            elevation = if (isSelected) 3 else 2,
                            isActive = isSelected,
                            shape = SalamShapes.cardMedium,
                            onClick = {
                                ThemeManager.setTheme(context, theme)
                                ThemeManager.saveTheme(context, theme)
                                if (theme == AppTheme.CUSTOM) {
                                    onNavigateToCustomTheme()
                                }
                            }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(SalamSpacing.cardPaddingInner),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = theme.displayName,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themePalette.textPrimary
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        swatches.forEach { color ->
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                                    .border(
                                                        width = 1.dp,
                                                        color = palette.textPrimary.copy(alpha = 0.15f),
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(themePalette.primary.copy(alpha = 0.4f))
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(10.dp)
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(palette.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = palette.background,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (rowThemes.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(SalamSpacing.cardGap))

            // AMOLED Pure Black toggle switch
            var amoledEnabled by remember { mutableStateOf(ThemeManager.amoledModeEnabled) }
            SalamCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntrance(themes.size),
                elevation = 2
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SalamSpacing.cardPaddingInner, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = LanguageManager.get("settings_amoled"),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                        )
                        Text(
                            text = "Absolute black for dark themes",
                            style = MaterialTheme.typography.labelMedium.copy(color = palette.textSecondary)
                        )
                    }
                    Switch(
                        checked = amoledEnabled,
                        onCheckedChange = { checked ->
                            amoledEnabled = checked
                            ThemeManager.setAmoledMode(context, checked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = palette.primary,
                            checkedTrackColor = palette.primary.copy(alpha = 0.3f),
                            uncheckedThumbColor = palette.textMuted,
                            uncheckedTrackColor = palette.surfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))

            // ── Calculation Method ────────────────────────────────────
            SalamSectionHeader(title = LanguageManager.get("settings_calc_method"))

            SalamCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2
            ) {
                Column(modifier = Modifier.padding(SalamSpacing.elementGap)) {
                    calculationMethods.forEach { (type, label) ->
                        val isSelected = method == type
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .salamClickable { viewModel.updateCalculationMethod(type) }
                                .padding(vertical = 12.dp, horizontal = SalamSpacing.cardPaddingInner),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.updateCalculationMethod(type) },
                                colors = RadioButtonDefaults.colors(selectedColor = palette.primary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) palette.textPrimary else palette.textSecondary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))

            // ── Juristic Method ──────────────────────────────────────
            SalamSectionHeader(title = LanguageManager.get("settings_juristic_method"))

            SalamCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2
            ) {
                Column(modifier = Modifier.padding(SalamSpacing.elementGap)) {
                    madhabs.forEach { (type, label) ->
                        val isSelected = madhab == type
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .salamClickable { viewModel.updateMadhab(type) }
                                .padding(vertical = 12.dp, horizontal = SalamSpacing.cardPaddingInner),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.updateMadhab(type) },
                                colors = RadioButtonDefaults.colors(selectedColor = palette.primary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) palette.textPrimary else palette.textSecondary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))

            // ── Notifications Preferences ────────────────────────────
            SalamSectionHeader(title = LanguageManager.get("settings_notifications"))

            SalamCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2
            ) {
                Column(modifier = Modifier.padding(SalamSpacing.cardPaddingInner)) {
                    val targetPrayers = listOf(
                        PrayerType.FAJR to "fajr",
                        PrayerType.DHUHR to "dhuhr",
                        PrayerType.ASR to "asr",
                        PrayerType.MAGHRIB to "maghrib",
                        PrayerType.ISHA to "isha",
                        PrayerType.QIYAM to "qiyam"
                    )

                    targetPrayers.forEachIndexed { index, (type, translationKey) ->
                        var activeMode by remember {
                            mutableStateOf(NotificationHelper.getPrayerNotificationMode(context, type))
                        }
                        var isExpanded by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = LanguageManager.get(translationKey),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textPrimary
                                    )
                                )
                                Text(
                                    text = LanguageManager.get("mode_${activeMode.lowercase()}"),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = when (activeMode) {
                                            "ATHAN" -> palette.primary
                                            "BEEP" -> palette.accent
                                            "SILENT" -> palette.textSecondary
                                            else -> palette.textMuted
                                        }
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Segmented Tabs: ATHAN, BEEP, SILENT, OFF
                            val modes = listOf("ATHAN", "BEEP", "SILENT", "OFF")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(palette.surfaceVariant.copy(alpha = 0.4f), SalamShapes.cardSmall)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                modes.forEach { mode ->
                                    val isSelected = activeMode == mode
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(34.dp)
                                            .background(
                                                color = if (isSelected) palette.primary else Color.Transparent,
                                                shape = SalamShapes.cardSmall
                                            )
                                            .salamClickable {
                                                activeMode = mode
                                                NotificationHelper.savePrayerNotificationMode(context, type, mode)
                                                NotificationHelper.schedulePrayerAlarms(context)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = LanguageManager.get("mode_${mode.lowercase()}"),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) palette.background else palette.textSecondary
                                            )
                                        )
                                    }
                                }
                            }

                            if (activeMode != "OFF") {
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Expandable Toggle Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(palette.surfaceVariant.copy(alpha = 0.25f), SalamShapes.cardSmall)
                                        .clickable { isExpanded = !isExpanded }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Reminder & Iqamah Settings",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textSecondary
                                        )
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Toggle Collapse",
                                        tint = palette.textMuted
                                    )
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp, start = 4.dp, end = 4.dp)
                                    ) {
                                        // --- PRE-PRAYER REMINDER ---
                                        Text(
                                            text = LanguageManager.get("pre_prayer_reminder"),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.textSecondary
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val preOffsets = listOf(0, 5, 10, 15, 30)
                                        var selectedPreOffset by remember {
                                            mutableStateOf(NotificationHelper.getPrePrayerOffset(context, type))
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            preOffsets.forEach { offset ->
                                                val isSelected = selectedPreOffset == offset
                                                val label = if (offset == 0) LanguageManager.get("none") else "$offset ${LanguageManager.get("minutes_before")}"
                                                FilterChip(
                                                    selected = isSelected,
                                                    onClick = {
                                                        selectedPreOffset = offset
                                                        NotificationHelper.savePrePrayerOffset(context, type, offset)
                                                        NotificationHelper.schedulePrayerAlarms(context)
                                                    },
                                                    label = { Text(label, fontSize = 11.sp) },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = palette.primary,
                                                        selectedLabelColor = palette.onPrimary,
                                                        containerColor = palette.surfaceVariant.copy(alpha = 0.3f),
                                                        labelColor = palette.textSecondary
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // --- IQAMAH ALERT ---
                                        Text(
                                            text = LanguageManager.get("iqamah_reminder"),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.textSecondary
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val iqamahOffsets = listOf(0, 5, 10, 15, 20, 30)
                                        var selectedIqamahOffset by remember {
                                            mutableStateOf(NotificationHelper.getIqamahOffset(context, type))
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            iqamahOffsets.forEach { offset ->
                                                val isSelected = selectedIqamahOffset == offset
                                                val label = if (offset == 0) LanguageManager.get("none") else "$offset ${LanguageManager.get("minutes_after")}"
                                                FilterChip(
                                                    selected = isSelected,
                                                    onClick = {
                                                        selectedIqamahOffset = offset
                                                        NotificationHelper.saveIqamahOffset(context, type, offset)
                                                        NotificationHelper.schedulePrayerAlarms(context)
                                                    },
                                                    label = { Text(label, fontSize = 11.sp) },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = palette.primary,
                                                        selectedLabelColor = palette.onPrimary,
                                                        containerColor = palette.surfaceVariant.copy(alpha = 0.3f),
                                                        labelColor = palette.textSecondary
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (index < targetPrayers.lastIndex) {
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = palette.outline.copy(alpha = 0.15f))
                        }
                    }
                }
            }

            // ── App Language ────────────────────────────────────────
            Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
            SalamSectionHeader(title = LanguageManager.get("settings_app_lang"))
            SalamCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(SalamSpacing.cardPaddingInner),
                    horizontalArrangement = Arrangement.spacedBy(SalamSpacing.elementGap)
                ) {
                    LanguageManager.languages.forEach { lang ->
                        val isSelected = currentThemeLang == lang.code
                        FilterChip(
                            selected = isSelected,
                            onClick = { 
                                LanguageManager.saveLanguage(context, lang.code)
                                viewModel.recalculate()
                            },
                            label = { Text(lang.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = palette.primary,
                                selectedLabelColor = palette.onPrimary,
                                containerColor = palette.surfaceVariant.copy(alpha = 0.5f),
                                labelColor = palette.textPrimary
                            )
                        )
                    }
                }
            }

            // ── Muezzin Voice ───────────────────────────────────────
            Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
            SalamSectionHeader(title = LanguageManager.get("settings_muezzin_voice"))
            SalamCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2
            ) {
                Column(modifier = Modifier.padding(SalamSpacing.elementGap)) {
                    val muezzins = listOf(
                        "mishary" to "Sheikh Mishary Al-Afasy",
                        "makkah" to "Makkah Athan (Masjid al-Haram)",
                        "madinah" to "Madinah Athan (Masjid an-Nabawi)"
                    )
                    muezzins.forEach { (code, name) ->
                        val isSelected = selectedMuezzin == code
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .salamClickable {
                                    selectedMuezzin = code
                                    prefs.edit().putString("athan_muezzin", code).apply()
                                }
                                .padding(vertical = 12.dp, horizontal = SalamSpacing.cardPaddingInner),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedMuezzin = code
                                    prefs.edit().putString("athan_muezzin", code).apply()
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = palette.primary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) palette.textPrimary else palette.textSecondary
                                )
                            )
                        }
                    }
                }
            }

            // ── About Section ────────────────────────────────────────
            Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
            SalamSectionHeader(title = LanguageManager.get("settings_about"))

            val updateStatus by com.yehia.prayertimes.utils.AutoUpdater.status.collectAsState()
            val latestVersion by com.yehia.prayertimes.utils.AutoUpdater.latestVersion.collectAsState()

            SalamCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = SalamSpacing.cardGap),
                elevation = 2
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SalamSpacing.cardPaddingInnerLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "App Updates",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (updateStatus) {
                            com.yehia.prayertimes.utils.AutoUpdater.UpdateStatus.IDLE -> "Check for updates from GitHub"
                            com.yehia.prayertimes.utils.AutoUpdater.UpdateStatus.CHECKING -> "Checking..."
                            com.yehia.prayertimes.utils.AutoUpdater.UpdateStatus.UPDATE_AVAILABLE -> "New version $latestVersion available!"
                            com.yehia.prayertimes.utils.AutoUpdater.UpdateStatus.UP_TO_DATE -> "Salam is up to date"
                            com.yehia.prayertimes.utils.AutoUpdater.UpdateStatus.DOWNLOADING -> "Downloading update..."
                            com.yehia.prayertimes.utils.AutoUpdater.UpdateStatus.ERROR -> "Error checking for updates"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (updateStatus == com.yehia.prayertimes.utils.AutoUpdater.UpdateStatus.UPDATE_AVAILABLE) {
                        Box(
                            modifier = Modifier
                                .background(palette.primary, SalamShapes.pill)
                                .clickable { com.yehia.prayertimes.utils.AutoUpdater.startUpdate(context) }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Install Update",
                                color = palette.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .background(palette.primary.copy(alpha = 0.12f), SalamShapes.pill)
                                .clickable(enabled = updateStatus != com.yehia.prayertimes.utils.AutoUpdater.UpdateStatus.CHECKING) {
                                    com.yehia.prayertimes.utils.AutoUpdater.checkForUpdates()
                                }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Check Now",
                                color = palette.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
        }
    }
}
