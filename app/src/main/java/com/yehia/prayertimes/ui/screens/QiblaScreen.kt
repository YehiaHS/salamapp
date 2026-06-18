package com.yehia.prayertimes.ui.screens

import java.util.Locale
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import com.yehia.prayertimes.ui.theme.SalamCard
import com.yehia.prayertimes.ui.theme.SalamTopBar
import com.yehia.prayertimes.ui.theme.SalamScreenScaffold
import com.yehia.prayertimes.ui.theme.SalamSpacing
import com.yehia.prayertimes.ui.theme.SalamShapes
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.theme.salamClickable
import com.yehia.prayertimes.ui.theme.rememberShortestAngleState
import com.yehia.prayertimes.ui.viewmodel.PrayerViewModel
import com.yehia.prayertimes.ui.theme.ThemePalette
import kotlin.math.abs

@Composable
fun QiblaScreen(
    viewModel: PrayerViewModel,
    onNavigateBack: () -> Unit
) {
    val palette = ThemeManager.currentPalette.collectAsState().value
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("salam_prefs", Context.MODE_PRIVATE) }
    var selectedPointerStyle by remember { mutableStateOf(prefs.getString("qibla_pointer_style", "classic") ?: "classic") }
    
    val uiState by viewModel.uiState.collectAsState()
    val deviceAzimuthState = viewModel.deviceAzimuth.collectAsState()
    val qiblaDirection = uiState?.qiblaDirection ?: 0.0

    // Lifecycle-aware sensor registration for compass
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val mGravity = FloatArray(3)
        val mGeomagnetic = FloatArray(3)
        var hasGravity = false
        var hasGeomagnetic = false

        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, mGravity, 0, event.values.size)
                    hasGravity = true
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, mGeomagnetic, 0, event.values.size)
                    hasGeomagnetic = true
                }

                if (hasGravity && hasGeomagnetic) {
                    val success = SensorManager.getRotationMatrix(
                        rotationMatrix,
                        null,
                        mGravity,
                        mGeomagnetic
                    )
                    if (success) {
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)
                        val azimuthInRadians = orientationAngles[0]
                        val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                        viewModel.updateDeviceAzimuth(azimuthInDegrees)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            gsensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
            msensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                gsensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
                msensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                sensorManager.unregisterListener(listener)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sensorManager.unregisterListener(listener)
        }
    }

    // Compute alignment as a derived state
    val isAlignedState = remember(qiblaDirection) {
        derivedStateOf {
            val currentAzimuth = deviceAzimuthState.value
            val relativeAngle = (qiblaDirection - currentAzimuth).toFloat()
            abs(relativeAngle % 360f) < 6f || abs(relativeAngle % 360f - 360f) < 6f
        }
    }
    val isAligned = isAlignedState.value

    SalamScreenScaffold(
        showGeometricPattern = false,
        backgroundBrush = SolidColor(palette.background)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            // 1. Anchored bottom sand dunes and mosque vector illustration background
            Image(
                painter = painterResource(id = com.yehia.prayertimes.R.drawable.bg_main_sand_mosque),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .graphicsLayer(alpha = 0.5f)
            )

            Column(modifier = Modifier.fillMaxSize()) {
                
                // 2. Custom header with Back icon, Options Menu icon, and serif titles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = palette.textPrimary
                        )
                    }
                    IconButton(onClick = { /* Open options dialog */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = palette.textPrimary
                        )
                    }
                }

                Text(
                    text = "Qibla",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = palette.textPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = String.format(Locale.US, "%.0f° NW", if (qiblaDirection < 0) qiblaDirection + 360f else qiblaDirection),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = palette.textSecondary,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))

                // 3. Visual Compass widget
                CompassWidget(
                    qiblaDirection = qiblaDirection,
                    deviceAzimuthState = deviceAzimuthState,
                    isAlignedState = isAlignedState,
                    selectedPointerStyle = selectedPointerStyle,
                    palette = palette,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Alignment Status Text Banner (Floating layout matching the design)
                if (isAligned) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(palette.primary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Aligned",
                                tint = palette.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You are facing the Qibla",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                        )
                        Text(
                            text = "Keep this direction in mind",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = palette.textSecondary
                            )
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Rotate your phone",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textSecondary
                            )
                        )
                        Text(
                            text = "Align the pointer to the top center",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = palette.textMuted
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Pointer design selector
                SalamCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 1,
                    shape = SalamShapes.cardMedium
                ) {
                    Column(modifier = Modifier.padding(SalamSpacing.cardPaddingInner)) {
                        Text(
                            text = "Compass Pointer Design",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = palette.textPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = SalamSpacing.elementGap)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(SalamSpacing.elementGap)
                        ) {
                            val designs = listOf(
                                "classic" to "Kaaba 3D",
                                "minimal" to "Minimal Line",
                                "emerald" to "Emerald Arrow",
                                "cosmic" to "Cosmic Portal"
                            )
                            designs.forEach { (code, name) ->
                                val isSelected = selectedPointerStyle == code
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) palette.primary.copy(alpha = 0.2f) else palette.surfaceVariant.copy(alpha = 0.3f),
                                            SalamShapes.cardSmall
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) palette.primary else Color.Transparent,
                                            shape = SalamShapes.cardSmall
                                        )
                                        .salamClickable {
                                            selectedPointerStyle = code
                                            prefs.edit().putString("qibla_pointer_style", code).apply()
                                        }
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) palette.primary else palette.textSecondary
                                        ),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SalamSpacing.screenPaddingV))
            }
        }
    }
}

@Composable
fun HeadingText(
    deviceAzimuthState: State<Float>,
    style: androidx.compose.ui.text.TextStyle
) {
    val deviceAzimuth = deviceAzimuthState.value
    Text(
        text = String.format(Locale.US, "%.1f°", if (deviceAzimuth < 0) deviceAzimuth + 360f else deviceAzimuth),
        style = style
    )
}

@Composable
fun CompassWidget(
    qiblaDirection: Double,
    deviceAzimuthState: State<Float>,
    isAlignedState: State<Boolean>,
    selectedPointerStyle: String,
    palette: ThemePalette,
    modifier: Modifier = Modifier
) {
    val isAligned = isAlignedState.value

    Box(
        modifier = modifier
            .size(280.dp)
            .drawBehind {
                if (isAligned) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(palette.glowColor.copy(alpha = 0.35f), Color.Transparent),
                            center = center,
                            radius = size.minDimension * 0.7f
                        )
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        CompassDial(
            deviceAzimuthState = deviceAzimuthState,
            palette = palette
        )

        CompassPointer(
            qiblaDirection = qiblaDirection,
            deviceAzimuthState = deviceAzimuthState,
            selectedPointerStyle = selectedPointerStyle,
            isAligned = isAligned,
            palette = palette
        )
    }
}

@Composable
fun CompassDial(
    deviceAzimuthState: State<Float>,
    palette: ThemePalette,
    modifier: Modifier = Modifier
) {
    val shortestAzimuth by rememberShortestAngleState(deviceAzimuthState.value)
    // Smooth elastic spring azimuth to emulate compass weight and physical inertia
    val animatedAzimuth by animateFloatAsState(
        targetValue = shortestAzimuth,
        animationSpec = spring(
            dampingRatio = 0.55f, // Physical jiggle overshoot
            stiffness = Spring.StiffnessMedium
        ),
        label = "dialAzimuth"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationZ = -animatedAzimuth
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = com.yehia.prayertimes.R.drawable.ic_compass_dial),
            contentDescription = "Compass Dial",
            modifier = Modifier.fillMaxSize()
        )
        
        // Cardinal direction overlay rotating with the dial
        Text(
            text = "N",
            fontWeight = FontWeight.Black,
            color = palette.primary,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
        )
        Text(
            text = "S",
            fontWeight = FontWeight.Bold,
            color = palette.textSecondary,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
        )
        Text(
            text = "E",
            fontWeight = FontWeight.Bold,
            color = palette.textSecondary,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 28.dp)
        )
        Text(
            text = "W",
            fontWeight = FontWeight.Bold,
            color = palette.textSecondary,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 28.dp)
        )
    }
}

@Composable
fun CompassPointer(
    qiblaDirection: Double,
    deviceAzimuthState: State<Float>,
    selectedPointerStyle: String,
    isAligned: Boolean,
    palette: ThemePalette,
    modifier: Modifier = Modifier
) {
    val shortestAzimuth by rememberShortestAngleState(deviceAzimuthState.value)
    // Dynamic compass needle physics interpolation
    val animatedAzimuth by animateFloatAsState(
        targetValue = shortestAzimuth,
        animationSpec = spring(
            dampingRatio = 0.55f, // Physical magnetic wobble
            stiffness = Spring.StiffnessMedium
        ),
        label = "pointerAzimuth"
    )

    Box(
        modifier = modifier
            .size(160.dp)
            .graphicsLayer {
                rotationZ = (qiblaDirection - animatedAzimuth).toFloat()
            },
        contentAlignment = Alignment.Center
    ) {
        when (selectedPointerStyle) {
            "minimal" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawLine(
                                color = if (isAligned) palette.primary else palette.textMuted,
                                start = center,
                                end = Offset(center.x, 15.dp.toPx()),
                                strokeWidth = 4.dp.toPx()
                            )
                            drawCircle(color = palette.primary, radius = 5.dp.toPx(), center = center)
                        }
                )
            }
            "emerald" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val path = Path().apply {
                                moveTo(center.x, 10.dp.toPx())
                                lineTo(center.x - 18.dp.toPx(), center.y + 25.dp.toPx())
                                lineTo(center.x, center.y + 12.dp.toPx())
                                lineTo(center.x + 18.dp.toPx(), center.y + 25.dp.toPx())
                                close()
                            }
                            drawPath(path = path, color = palette.primary)
                        }
                )
            }
            "cosmic" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(color = palette.secondary.copy(alpha = 0.2f), radius = 45.dp.toPx(), center = center)
                            drawCircle(color = palette.primary.copy(alpha = 0.4f), radius = 25.dp.toPx(), center = center, style = Stroke(width = 2.dp.toPx()))
                            drawLine(color = palette.accent, start = center, end = Offset(center.x, 15.dp.toPx()), strokeWidth = 3.dp.toPx())
                            drawCircle(color = palette.accent, radius = 6.dp.toPx(), center = center)
                        }
                )
            }
            else -> { // "classic" - 3D Kaaba art compass pointer as requested
                Image(
                    painter = painterResource(id = com.yehia.prayertimes.R.drawable.ic_qibla_kaaba),
                    contentDescription = "Kaaba Pointer",
                    modifier = Modifier.size(110.dp)
                )
                
                // Overlay violet triangle pointing UP on the pointer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val sizePx = 14.dp.toPx()
                            val path = Path().apply {
                                moveTo(center.x, 6.dp.toPx())
                                lineTo(center.x - sizePx / 2, 6.dp.toPx() + sizePx)
                                lineTo(center.x + sizePx / 2, 6.dp.toPx() + sizePx)
                                close()
                            }
                            drawPath(path = path, color = palette.primary)
                        }
                )
            }
        }
    }
}
