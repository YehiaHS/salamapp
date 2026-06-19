package com.yehia.prayertimes.ui.screens

import android.content.Context
import android.view.SoundEffectConstants
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import com.yehia.prayertimes.ui.theme.AmiriFontFamily
import com.yehia.prayertimes.ui.theme.SalamCard
import com.yehia.prayertimes.ui.theme.SalamScreenScaffold
import com.yehia.prayertimes.ui.theme.SalamShapes
import com.yehia.prayertimes.ui.theme.SalamSpacing
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.theme.ThemePalette
import com.yehia.prayertimes.ui.theme.salamClickable
import com.yehia.prayertimes.ui.theme.staggeredEntrance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DhikrScreen() {
    val palette by ThemeManager.currentPalette.collectAsState()
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("salam_prefs", Context.MODE_PRIVATE) }

    // Persisted preferences
    var selectedBeadType by remember { mutableStateOf(prefs.getString("dhikr_bead_type", "wood") ?: "wood") }
    var activePresetIndex by remember { mutableStateOf(prefs.getInt("dhikr_active_preset_idx", 0)) }
    var count by remember { mutableStateOf(prefs.getInt("dhikr_current_count", 0)) }
    var selectedTarget by remember { mutableStateOf(prefs.getInt("dhikr_selected_target", 33)) }
    var autoAdvanceEnabled by remember { mutableStateOf(prefs.getBoolean("dhikr_auto_advance", true)) }
    var soundEnabled by remember { mutableStateOf(prefs.getBoolean("dhikr_sound_enabled", true)) }
    var hapticEnabled by remember { mutableStateOf(prefs.getBoolean("dhikr_haptic_enabled", true)) }

    LaunchedEffect(count) {
        if (count == 0) {
            prefs.edit().putInt("dhikr_current_count", 0).apply()
        } else {
            delay(1000L)
            prefs.edit().putInt("dhikr_current_count", count).apply()
        }
    }

    var customTargetInput by remember { mutableStateOf("") }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showSettingsPopup by remember { mutableStateOf(false) }
    var isCelebrated by remember { mutableStateOf(false) }

    // Click motion animation (horizontal lines expand)
    val clickAnimation = remember { Animatable(0f) }

    // Supplications
    val presets = listOf(
        Triple("سُبْحَانَ اللَّهِ", "SubhanAllah", "Glory be to Allah"),
        Triple("الْحَمْدُ لِلَّهِ", "Alhamdulillah", "Praise be to Allah"),
        Triple("اللَّهُ أَكْبَرُ", "Allahu Akbar", "Allah is the Greatest"),
        Triple("لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illallah", "There is no god but Allah")
    )

    val clickHapticType = when (selectedBeadType) {
        "wood" -> HapticFeedbackType.TextHandleMove
        else -> HapticFeedbackType.LongPress
    }

    val screenInteractionSource = remember { MutableInteractionSource() }

    SalamScreenScaffold(showGeometricPattern = false) {
        // Top Bar: Title + Subtitle on Left, Context Menu Button on Right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = SalamSpacing.cardPaddingInner, end = SalamSpacing.cardPaddingInner, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Dhikr",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = palette.textPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = "Tasbih Counter",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = palette.textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            IconButton(
                onClick = { showSettingsPopup = !showSettingsPopup },
                modifier = Modifier
                    .size(44.dp)
                    .background(palette.surfaceVariant.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Settings",
                    tint = palette.textPrimary
                )
            }
        }

        // Main Area containing S-Curve and Text Overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable(
                    interactionSource = screenInteractionSource,
                    indication = null,
                    onClickLabel = "Tap to increment Tasbih count",
                    role = androidx.compose.ui.semantics.Role.Button
                ) {
                    if (count < selectedTarget) {
                        count++

                        if (soundEnabled) {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                        }

                        scope.launch {
                            clickAnimation.snapTo(1f)
                            clickAnimation.animateTo(0f, animationSpec = tween(300))
                        }

                        if (count == selectedTarget) {
                            if (hapticEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            isCelebrated = true
                            scope.launch {
                                delay(600)
                                isCelebrated = false
                                if (autoAdvanceEnabled) {
                                    activePresetIndex = (activePresetIndex + 1) % presets.size
                                    prefs.edit().putInt("dhikr_active_preset_idx", activePresetIndex).apply()
                                    count = 0
                                }
                            }
                        } else {
                            if (hapticEnabled) {
                                haptic.performHapticFeedback(clickHapticType)
                            }
                        }
                    }
                }
        ) {
            val density = LocalDensity.current
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val widthPx = with(density) { maxWidth.toPx() }
                val heightPx = with(density) { maxHeight.toPx() }

                // Curve measurements & caching
                val pathData = remember(widthPx, heightPx) {
                    val path = Path().apply {
                        moveTo(widthPx * 0.12f, heightPx * 0.75f)
                        cubicTo(
                            widthPx * 0.35f, heightPx * 0.78f,
                            widthPx * 0.88f, heightPx * 0.68f,
                            widthPx * 0.84f, heightPx * 0.55f
                        )
                        cubicTo(
                            widthPx * 0.80f, heightPx * 0.40f,
                            widthPx * 0.38f, heightPx * 0.44f,
                            widthPx * 0.48f, heightPx * 0.32f
                        )
                        cubicTo(
                            widthPx * 0.58f, heightPx * 0.20f,
                            widthPx * 0.90f, heightPx * 0.24f,
                            widthPx * 1.15f, heightPx * 0.20f
                        )
                    }
                    val pathMeasure = android.graphics.PathMeasure(path.asAndroidPath(), false)
                    Pair(path, pathMeasure)
                }

                val path = pathData.first
                val pathMeasure = pathData.second

                // Drawing S-curve, beads, tassel, and indicator double arrows
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pathLength = pathMeasure.length

                    fun getPointOnPath(t: Float): Offset {
                        val pos = FloatArray(2)
                        pathMeasure.getPosTan(pathLength * t.coerceIn(0f, 1f), pos, null)
                        return Offset(pos[0], pos[1])
                    }

                    // 1. Draw thread line
                    drawPath(
                        path = path,
                        color = palette.primary.copy(alpha = 0.2f),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // 2. Draw Tassel at the start of the path (t = 0)
                    val startPos = FloatArray(2)
                    val startTan = FloatArray(2)
                    pathMeasure.getPosTan(0f, startPos, startTan)
                    val startPoint = Offset(startPos[0], startPos[1])
                    val tasselDirX = -startTan[0]
                    val tasselDirY = -startTan[1]
                    val tasselLength = 44.dp.toPx()

                    // Tassel cap and threads matching bead theme color
                    val tasselColor = when (selectedBeadType) {
                        "pearl" -> Color(0xFFD9D9D9)
                        "clay" -> Color(0xFFC0523B)
                        "wood" -> Color(0xFFB58253)
                        else -> palette.primary
                    }

                    drawCircle(
                        color = tasselColor,
                        radius = 8.dp.toPx(),
                        center = startPoint
                    )
                    // Tassel threads
                    for (j in -3..3) {
                        val angleOffset = j * 0.12f
                        val dx = tasselDirX * kotlin.math.cos(angleOffset) - tasselDirY * kotlin.math.sin(angleOffset)
                        val dy = tasselDirX * kotlin.math.sin(angleOffset) + tasselDirY * kotlin.math.cos(angleOffset)
                        val endPoint = startPoint + Offset(dx * tasselLength, dy * tasselLength)
                        drawLine(
                            color = tasselColor.copy(alpha = 0.8f),
                            start = startPoint,
                            end = endPoint,
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // 3. Draw sliding beads
                    val activeBeadT = 0.58f  // Active position on path
                    val beadSpacing = 0.038f // Spacing fraction along path
                    val baseRadius = 20.dp.toPx()

                    // Fetch animated sliding progress
                    val currentFloat = count.toFloat() - clickAnimation.value

                    // Draw loop for beads (from count - 16 to count + 16)
                    for (i in (currentFloat.toInt() - 16)..(currentFloat.toInt() + 16)) {
                        val t = activeBeadT + (i - currentFloat) * beadSpacing
                        if (t < 0f || t > 1f) continue

                        val dist = kotlin.math.abs(t - activeBeadT)
                        val scale = 1f - dist.coerceIn(0f, 0.5f)
                        val celebratedScale = if (isCelebrated && i == count) 1.25f else 1f
                        val radius = baseRadius * scale * celebratedScale
                        val alpha = 1f - dist.coerceIn(0f, 0.8f)

                        val beadCenter = getPointOnPath(t)

                        // Bead style colors
                        when (selectedBeadType) {
                            "pearl" -> {
                                val pearlBrush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFE5E5EC), Color(0xFFB4B4C4)),
                                    center = Offset(beadCenter.x - radius * 0.3f, beadCenter.y - radius * 0.3f),
                                    radius = radius * 1.2f
                                )
                                drawCircle(
                                    brush = pearlBrush,
                                    radius = radius,
                                    center = beadCenter,
                                    alpha = alpha
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.8f * alpha),
                                    radius = radius * 0.16f,
                                    center = Offset(beadCenter.x - radius * 0.35f, beadCenter.y - radius * 0.35f)
                                )
                            }
                            "neon" -> {
                                val glowBrush = Brush.radialGradient(
                                    colors = listOf(palette.glowColor, palette.primary, palette.primary.copy(alpha = 0f)),
                                    center = beadCenter,
                                    radius = radius * 1.5f
                                )
                                drawCircle(
                                    brush = glowBrush,
                                    radius = radius * 1.4f,
                                    center = beadCenter,
                                    alpha = alpha * 0.45f
                                )
                                drawCircle(
                                    color = palette.primary,
                                    radius = radius,
                                    center = beadCenter,
                                    alpha = alpha
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.9f * alpha),
                                    radius = radius * 0.28f,
                                    center = beadCenter
                                )
                            }
                            "clay" -> {
                                val clayBrush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFE2725B), Color(0xFFC0523B), Color(0xFF6E2819)),
                                    center = Offset(beadCenter.x - radius * 0.2f, beadCenter.y - radius * 0.2f),
                                    radius = radius * 1.1f
                                )
                                drawCircle(
                                    brush = clayBrush,
                                    radius = radius,
                                    center = beadCenter,
                                    alpha = alpha
                                )
                            }
                            else -> { // "wood"
                                val woodBrush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFD4A373), Color(0xFFB58253), Color(0xFF5C3D2E)),
                                    center = Offset(beadCenter.x - radius * 0.2f, beadCenter.y - radius * 0.2f),
                                    radius = radius * 1.1f
                                )
                                drawCircle(
                                    brush = woodBrush,
                                    radius = radius,
                                    center = beadCenter,
                                    alpha = alpha
                                )
                                drawCircle(
                                    color = Color(0x203C2210),
                                    radius = radius * 0.65f,
                                    center = beadCenter,
                                    style = Stroke(width = 1f),
                                    alpha = alpha
                                )
                            }
                        }
                    }

                    // 4. Draw Indicator Trail `<- ... ->` to the left of the active bead
                    val activePos = getPointOnPath(activeBeadT)
                    val trailStartX = activePos.x - 70.dp.toPx()
                    val trailEndX = activePos.x - 14.dp.toPx()
                    val trailY = activePos.y

                    val trailColor = palette.textSecondary.copy(alpha = 0.5f)
                    val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

                    // Dashed line
                    drawLine(
                        color = trailColor,
                        start = Offset(trailStartX, trailY),
                        end = Offset(trailEndX, trailY),
                        strokeWidth = 2f,
                        pathEffect = dashPathEffect
                    )

                    // Double arrow heads
                    val arrowSize = 6.dp.toPx()
                    // Left Arrow
                    drawLine(color = trailColor, start = Offset(trailStartX, trailY), end = Offset(trailStartX + arrowSize, trailY - arrowSize), strokeWidth = 2f)
                    drawLine(color = trailColor, start = Offset(trailStartX, trailY), end = Offset(trailStartX + arrowSize, trailY + arrowSize), strokeWidth = 2f)
                    // Right Arrow
                    drawLine(color = trailColor, start = Offset(trailEndX, trailY), end = Offset(trailEndX - arrowSize, trailY - arrowSize), strokeWidth = 2f)
                    drawLine(color = trailColor, start = Offset(trailEndX, trailY), end = Offset(trailEndX - arrowSize, trailY + arrowSize), strokeWidth = 2f)

                    // 5. Draw Motion Blur / Swipe Trail on Click
                    if (clickAnimation.value > 0f) {
                        val blurLength = 32.dp.toPx() * clickAnimation.value
                        val blurBrush = Brush.horizontalGradient(
                            colors = listOf(palette.primary.copy(alpha = 0.4f * clickAnimation.value), Color.Transparent),
                            startX = activePos.x,
                            endX = activePos.x + blurLength
                        )
                        drawCircle(
                            brush = blurBrush,
                            radius = baseRadius * (if (isCelebrated) 1.25f else 1f) * 1.1f,
                            center = activePos
                        )
                    }
                }
            }

            // Left-Side Column Overlay (Counter, Denominator, Supplication, Progress Card)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(190.dp)
                    .padding(start = SalamSpacing.cardPaddingInner),
                verticalArrangement = Arrangement.Center
            ) {
                // Supplication text aligned, without pill container or emoji
                Text(
                    text = presets[activePresetIndex].first,
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = palette.primary,
                    modifier = Modifier.padding(start = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Massive Numeric Count Text
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 96.sp,
                        fontWeight = FontWeight.Black,
                        color = palette.textPrimary,
                        lineHeight = 96.sp
                    )
                )

                // Sub-target denominator
                Text(
                    text = "/ $selectedTarget",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = palette.textSecondary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(start = 6.dp)
                )

            }

            // Top-Right Settings Popup Dialog Card
            androidx.compose.animation.AnimatedVisibility(
                visible = showSettingsPopup,
                enter = fadeIn() + scaleIn(transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 0f)),
                exit = fadeOut() + scaleOut(transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 0f)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 4.dp)
                    .width(220.dp)
            ) {
                Card(
                    shape = SalamShapes.cardMedium,
                    colors = CardDefaults.cardColors(containerColor = palette.surface),
                    border = BorderStroke(1.dp, palette.outline.copy(alpha = 0.35f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // 1. Bead Style Selection
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = "Bead Style",
                                tint = palette.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bead Style",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = palette.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp)
                        ) {
                            val beads = listOf(
                                "wood" to listOf(Color(0xFFCD853F), Color(0xFF8B5A2B)),
                                "pearl" to listOf(Color(0xFFFFFDF9), Color(0xFFD9D9D9)),
                                "neon" to listOf(palette.primary, palette.glowColor),
                                "clay" to listOf(Color(0xFFD2691E), Color(0xFF8B4513))
                            )
                            beads.forEach { (code, colors) ->
                                val isSelected = selectedBeadType == code
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(Brush.radialGradient(colors), CircleShape)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) palette.primary else Color.White.copy(alpha = 0.3f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            selectedBeadType = code
                                            prefs.edit().putString("dhikr_bead_type", code).apply()
                                        }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. Goal Selection (cycles 33 -> 99 -> custom)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (selectedTarget) {
                                        33 -> {
                                            selectedTarget = 99
                                            prefs.edit().putInt("dhikr_selected_target", 99).apply()
                                            count = 0
                                        }
                                        99 -> {
                                            showCustomDialog = true
                                        }
                                        else -> {
                                            selectedTarget = 33
                                            prefs.edit().putInt("dhikr_selected_target", 33).apply()
                                            count = 0
                                        }
                                    }
                                }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Adjust,
                                    contentDescription = "Goal",
                                    tint = palette.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Goal",
                                    style = MaterialTheme.typography.labelLarge.copy(color = palette.textPrimary)
                                )
                            }
                            Text(
                                text = selectedTarget.toString(),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = palette.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3. Auto Advance
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "Auto Advance",
                                    tint = palette.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Auto Advance",
                                    style = MaterialTheme.typography.labelLarge.copy(color = palette.textPrimary)
                                )
                            }
                            Switch(
                                checked = autoAdvanceEnabled,
                                onCheckedChange = { checked ->
                                    autoAdvanceEnabled = checked
                                    prefs.edit().putBoolean("dhikr_auto_advance", checked).apply()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = palette.primary,
                                    checkedTrackColor = palette.primary.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.scale(0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 4. Sound FX
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = "Sound",
                                    tint = palette.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sound",
                                    style = MaterialTheme.typography.labelLarge.copy(color = palette.textPrimary)
                                )
                            }
                            Switch(
                                checked = soundEnabled,
                                onCheckedChange = { checked ->
                                    soundEnabled = checked
                                    prefs.edit().putBoolean("dhikr_sound_enabled", checked).apply()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = palette.primary,
                                    checkedTrackColor = palette.primary.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.scale(0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 5. Haptics
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Vibration,
                                    contentDescription = "Haptics",
                                    tint = palette.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Haptics",
                                    style = MaterialTheme.typography.labelLarge.copy(color = palette.textPrimary)
                                )
                            }
                            Switch(
                                checked = hapticEnabled,
                                onCheckedChange = { checked ->
                                    hapticEnabled = checked
                                    prefs.edit().putBoolean("dhikr_haptic_enabled", checked).apply()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = palette.primary,
                                    checkedTrackColor = palette.primary.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.scale(0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = palette.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(6.dp))

                        // 6. Reset Counter
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showSettingsPopup = false
                                    if (count > 0) {
                                        showResetDialog = true
                                    }
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = palette.accent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reset Counter",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = palette.textPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 7. About Dhikr
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showSettingsPopup = false
                                    showAboutDialog = true
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "About",
                                tint = palette.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "About Dhikr",
                                style = MaterialTheme.typography.labelLarge.copy(color = palette.textSecondary)
                            )
                        }
                    }
                }
            }
        }
    }

    // Custom Goal target input
    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text("Set Custom Target", color = palette.textPrimary) },
            text = {
                OutlinedTextField(
                    value = customTargetInput,
                    onValueChange = { customTargetInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Target count") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = palette.primary,
                        focusedLabelColor = palette.primary,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val inputInt = customTargetInput.toIntOrNull()
                        if (inputInt != null && inputInt > 0) {
                            selectedTarget = inputInt
                            prefs.edit().putInt("dhikr_selected_target", inputInt).apply()
                            count = 0
                        }
                        showCustomDialog = false
                    }
                ) {
                    Text("OK", color = palette.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) {
                    Text("Cancel", color = palette.textSecondary)
                }
            },
            containerColor = palette.surface
        )
    }

    // Reset verification dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Tasbih?", color = palette.textPrimary) },
            text = { Text("Are you sure you want to reset the current counter to 0?", color = palette.textSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        count = 0
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = palette.accent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Keep counting", color = palette.textSecondary)
                }
            },
            containerColor = palette.surface
        )
    }

    // About Dhikr dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Tasbih", color = palette.textPrimary) },
            text = {
                Text(
                    text = "Dhikr (remembrance of Allah) is the essence of worship. This interactive digital tasbih simulates a curved bead loop. Tap anywhere on the screen to pull the beads along the string. You can configure custom goals, sound alerts, and haptic feedback via the menu in the top-right.",
                    color = palette.textSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = palette.primary)
                }
            },
            containerColor = palette.surface
        )
    }
}
