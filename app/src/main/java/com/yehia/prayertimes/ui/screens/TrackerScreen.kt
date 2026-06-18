package com.yehia.prayertimes.ui.screens

import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    onNavigateBack: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val prefs = remember { context.getSharedPreferences("salam_prefs", Context.MODE_PRIVATE) }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }

    // Salah Tracker States
    val prayers = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
    val salahStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            prayers.forEach { prayer ->
                put(prayer, prefs.getBoolean("salah_${todayStr}_$prayer", false))
            }
        }
    }

    // Fasting Tracker State
    var isFastingToday by remember { mutableStateOf(prefs.getBoolean("fasting_$todayStr", false)) }
    var totalFastingDays by remember { mutableStateOf(prefs.getInt("total_fasting_days", 5)) }

    // Iftar/Sahur Countdown Timer State
    val timeRemainingStrState = remember { mutableStateOf("") }
    val timerTitleState = remember { mutableStateOf("Time remaining to Iftar") }

    LaunchedEffect(key1 = isFastingToday) {
        while (true) {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentSecond = calendar.get(Calendar.SECOND)

            if (isFastingToday) {
                // Iftar target is 19:00 (7 PM)
                if (currentHour < 19) {
                    timerTitleState.value = "Time remaining to Iftar"
                    val diffHrs = 18 - currentHour
                    val diffMins = 59 - currentMinute
                    val diffSecs = 59 - currentSecond
                    timeRemainingStrState.value = "%02dh %02dm %02ds".format(diffHrs, diffMins, diffSecs)
                } else {
                    timerTitleState.value = "Completed! Sahur starts in"
                    val diffHrs = 27 - currentHour
                    val diffMins = 59 - currentMinute
                    val diffSecs = 59 - currentSecond
                    timeRemainingStrState.value = "%02dh %02dm %02ds".format(diffHrs % 24, diffMins, diffSecs)
                }
            } else {
                timerTitleState.value = "Salah & Fasting Tracker"
                timeRemainingStrState.value = "Tap Fasting below to activate"
            }
            kotlinx.coroutines.delay(1000)
        }
    }

    SalamScreenScaffold {
        SalamTopBar(
            title = "Worship Tracker",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
        ) {
            Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

            // Hero Status & Countdown Card
            TrackerHero(
                timerTitleState = timerTitleState,
                timeRemainingStrState = timeRemainingStrState,
                salahStates = salahStates,
                totalFastingDays = totalFastingDays,
                palette = palette,
                modifier = Modifier.staggeredEntrance(0)
            )

            // ── Salah Checklist ─────────────────────────────────────
            SalamSectionHeader(
                title = "Daily Prayers",
                modifier = Modifier.staggeredEntrance(1)
            )

            SalamCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntrance(2),
                elevation = 2,
                shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner1
            ) {
                Column(modifier = Modifier.padding(SalamSpacing.elementGap)) {
                    prayers.forEachIndexed { index, prayer ->
                        val isChecked = salahStates[prayer] ?: false
                        val checkScale by animateFloatAsState(
                            targetValue = if (isChecked) 1.15f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "checkScale"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .staggeredEntrance(index + 3)
                                .salamClickable {
                                    val nextState = !isChecked
                                    salahStates[prayer] = nextState
                                    prefs.edit().putBoolean("salah_${todayStr}_$prayer", nextState).apply()
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                .padding(vertical = 12.dp, horizontal = SalamSpacing.cardPaddingInner),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(palette.primary.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = palette.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(SalamSpacing.cardPaddingInner))
                                Text(
                                    text = prayer,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textPrimary
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .graphicsLayer {
                                        scaleX = checkScale
                                        scaleY = checkScale
                                    }
                                    .clip(CircleShape)
                                    .background(if (isChecked) palette.primary else palette.surfaceVariant)
                                    .border(
                                        width = 1.dp,
                                        color = if (isChecked) palette.primary else palette.outline.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Checked",
                                        tint = palette.background,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Fasting Tracker ─────────────────────────────────────
            SalamSectionHeader(
                title = "Fasting Logs",
                modifier = Modifier.staggeredEntrance(9)
            )

            SalamCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntrance(10),
                elevation = 2,
                shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner2
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SalamSpacing.cardPaddingInnerLarge),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(palette.primary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestaurantMenu,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(SalamSpacing.cardPaddingInner))
                        Column {
                            Text(
                                text = "Fasting Today",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                            )
                            Text(
                                text = "Turn on if you are observing fast",
                                style = MaterialTheme.typography.labelSmall.copy(color = palette.textSecondary)
                            )
                        }
                    }
                    Switch(
                        checked = isFastingToday,
                        onCheckedChange = { checked ->
                            isFastingToday = checked
                            prefs.edit().putBoolean("fasting_$todayStr", checked).apply()

                            val updatedDays = if (checked) totalFastingDays + 1 else (totalFastingDays - 1).coerceAtLeast(0)
                            totalFastingDays = updatedDays
                            prefs.edit().putInt("total_fasting_days", updatedDays).apply()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
        }
    }
}

@Composable
fun TrackerHero(
    timerTitleState: State<String>,
    timeRemainingStrState: State<String>,
    salahStates: Map<String, Boolean>,
    totalFastingDays: Int,
    palette: com.yehia.prayertimes.ui.theme.ThemePalette,
    modifier: Modifier = Modifier
) {
    SalamCard(
        modifier = modifier.fillMaxWidth(),
        elevation = 3,
        shape = com.yehia.prayertimes.ui.theme.SalamShapes.squircle
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SalamSpacing.cardPaddingInnerLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimerTitleText(timerTitleState = timerTitleState, palette = palette)
            Spacer(modifier = Modifier.height(SalamSpacing.elementGap))
            RemainingTimeText(
                timeRemainingStrState = timeRemainingStrState,
                palette = palette,
                modifier = Modifier.breathingGlow()
            )
            Spacer(modifier = Modifier.height(SalamSpacing.cardPaddingInner))
            HorizontalDivider(color = palette.outline.copy(alpha = 0.2f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(SalamSpacing.cardPaddingInner))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val doneCount = salahStates.values.count { it }
                    Text("Salah Completed", style = MaterialTheme.typography.labelSmall.copy(color = palette.textMuted))
                    Text("$doneCount / 5", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = palette.textPrimary))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Fasting Days", style = MaterialTheme.typography.labelSmall.copy(color = palette.textMuted))
                    Text("$totalFastingDays days", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = palette.textPrimary))
                }
            }
        }
    }
}

@Composable
fun TimerTitleText(timerTitleState: State<String>, palette: com.yehia.prayertimes.ui.theme.ThemePalette) {
    Text(
        text = timerTitleState.value,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = palette.textSecondary,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun RemainingTimeText(
    timeRemainingStrState: State<String>,
    palette: com.yehia.prayertimes.ui.theme.ThemePalette,
    modifier: Modifier = Modifier
) {
    Text(
        text = timeRemainingStrState.value,
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Black,
            color = palette.primary
        ),
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}
