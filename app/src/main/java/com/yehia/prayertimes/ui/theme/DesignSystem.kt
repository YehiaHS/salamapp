package com.yehia.prayertimes.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────────────
// SPACING SYSTEM
// ─────────────────────────────────────────────────
object SalamSpacing {
    val screenPaddingH: Dp = 24.dp      // Horizontal outer padding
    val screenPaddingV: Dp = 20.dp      // Vertical outer padding (top)
    val cardPaddingInner: Dp = 16.dp    // Inner card padding
    val cardPaddingInnerLarge: Dp = 20.dp
    val cardGap: Dp = 12.dp            // Gap between cards
    val sectionGap: Dp = 24.dp         // Gap between sections
    val elementGap: Dp = 8.dp          // Gap between small elements
    val iconSize: Dp = 22.dp           // Standard icon size
    val iconSizeLarge: Dp = 28.dp
    val iconBackgroundSize: Dp = 44.dp  // Icon circle background
    val touchTarget: Dp = 48.dp        // Minimum touch target
}

// ─────────────────────────────────────────────────
// SHAPE SYSTEM
// ─────────────────────────────────────────────────
object SalamShapes {
    val cardLarge = RoundedCornerShape(28.dp)
    val cardMedium = RoundedCornerShape(22.dp)
    val cardSmall = RoundedCornerShape(16.dp)
    val pill = RoundedCornerShape(50)
    val circle = CircleShape
    // Material 3 Expressive shapes (overridden with symmetric Apple-style rectangular squircles)
    val expressiveCorner1 = RoundedCornerShape(20.dp)
    val expressiveCorner2 = RoundedCornerShape(16.dp)
    val squircle = RoundedCornerShape(34.dp)
}

// ─────────────────────────────────────────────────
// BOUNCE CLICK MODIFIER — Spring-based tap feedback
// ─────────────────────────────────────────────────
fun Modifier.bounceClick(interactionSource: MutableInteractionSource) = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounceScale"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

// ─────────────────────────────────────────────────
// SALAM CLICKABLE — Integrates spring tap bounce + ripple indication
// ─────────────────────────────────────────────────
fun Modifier.salamClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    salamClickable(
        interactionSource = interactionSource,
        enabled = enabled,
        onClick = onClick
    )
}

fun Modifier.salamClickable(
    interactionSource: MutableInteractionSource,
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.93f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "salamClickScale"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.clickable(
        interactionSource = interactionSource,
        indication = androidx.compose.foundation.LocalIndication.current,
        enabled = enabled,
        onClick = onClick
    )
}

// ─────────────────────────────────────────────────
// STAGGERED ENTRANCE — Combined fade-in and vertical slide spring jiggle
// ─────────────────────────────────────────────────
fun Modifier.staggeredEntrance(index: Int, delayMillis: Long = 45L) = composed {
    var hasEntered by rememberSaveable { mutableStateOf(false) }
    if (hasEntered) {
        this
    } else {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(index * delayMillis)
            visible = true
            kotlinx.coroutines.delay(450L) // Wait for completion of animation
            hasEntered = true
        }
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(durationMillis = 400, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            label = "staggeredAlpha"
        )
        val translationY by animateFloatAsState(
            targetValue = if (visible) 0f else 40f,
            animationSpec = spring(
                dampingRatio = 0.65f, // Low-damping bounce/jiggle
                stiffness = Spring.StiffnessMedium
            ),
            label = "staggeredTranslationY"
        )
        this.graphicsLayer {
            this.alpha = alpha
            this.translationY = translationY
        }
    }
}

// ─────────────────────────────────────────────────
// SHORTEST ANGLE STATE — Resolves compass wrapping bugs
// ─────────────────────────────────────────────────
@Composable
fun rememberShortestAngleState(targetAngle: Float): State<Float> {
    val accumulatedAngle = remember { mutableStateOf(targetAngle) }
    var prevTarget by remember { mutableStateOf(targetAngle) }

    LaunchedEffect(targetAngle) {
        val diff = targetAngle - prevTarget
        val normalizedDiff = ((diff + 180f) % 360f + 360f) % 360f - 180f
        accumulatedAngle.value += normalizedDiff
        prevTarget = targetAngle
    }
    return accumulatedAngle
}

// ─────────────────────────────────────────────────
// SALAM CARD — Enforces consistent styling
// ─────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalamCard(
    modifier: Modifier = Modifier,
    elevation: Int = 2,          // 1, 2, or 3 — maps to cardElevation tiers
    isActive: Boolean = false,
    shape: androidx.compose.ui.graphics.Shape = SalamShapes.expressiveCorner1,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = LocalThemePalette.current

    val containerColor = if (isActive) {
        palette.primary.copy(alpha = if (palette.isLight) 0.12f else 0.18f)
    } else {
        val base = when (elevation) {
            1 -> palette.cardElevation1
            3 -> palette.cardElevation3
            else -> palette.cardElevation2
        }
        base.copy(alpha = if (palette.isLight) 0.94f else 0.9f)
    }

    val border = if (isActive) {
        BorderStroke(
            width = 1.25.dp,
            brush = Brush.linearGradient(
                listOf(
                    palette.primary.copy(alpha = 0.9f),
                    palette.secondary.copy(alpha = 0.7f),
                    palette.accent.copy(alpha = 0.55f)
                )
            )
        )
    } else {
        BorderStroke(1.dp, palette.outline.copy(alpha = if (palette.isLight) 0.55f else 0.42f))
    }

    val cardModifier = modifier

    val shadowElevation = when (elevation) {
        1 -> 2.dp
        3 -> 10.dp
        else -> 5.dp
    }

    if (onClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        Card(
            modifier = cardModifier
                .bounceClick(interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = androidx.compose.foundation.LocalIndication.current,
                    onClick = onClick
                ),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = border,
            elevation = CardDefaults.cardElevation(
                defaultElevation = shadowElevation,
                pressedElevation = shadowElevation * 0.5f,
                focusedElevation = shadowElevation,
                hoveredElevation = shadowElevation
            ),
            content = content
        )
    } else {
        Card(
            modifier = cardModifier,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = shadowElevation),
            content = content
        )
    }
}

// ─────────────────────────────────────────────────
// SALAM SCREEN SCAFFOLD — Consistent screen wrapper
// ─────────────────────────────────────────────────
@Composable
fun SalamScreenScaffold(
    modifier: Modifier = Modifier,
    showGeometricPattern: Boolean = true,
    backgroundBrush: Brush? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (backgroundBrush != null) Modifier.background(backgroundBrush)
                else Modifier.background(palette.background)
            )
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.primary.copy(alpha = if (palette.isLight) 0.075f else 0.13f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.9f, size.height * 0.04f),
                        radius = size.minDimension * 0.58f
                    ),
                    radius = size.minDimension * 0.58f,
                    center = Offset(size.width * 0.9f, size.height * 0.04f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.accent.copy(alpha = if (palette.isLight) 0.06f else 0.1f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.02f, size.height * 0.92f),
                        radius = size.minDimension * 0.44f
                    ),
                    radius = size.minDimension * 0.44f,
                    center = Offset(size.width * 0.02f, size.height * 0.92f)
                )
            }
    ) {
        if (showGeometricPattern) {
            GeometricPatternLayer(palette)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = SalamSpacing.screenPaddingH,
                    end = SalamSpacing.screenPaddingH,
                    top = SalamSpacing.screenPaddingV,
                    bottom = 0.dp
                ),
            content = content
        )
    }
}

@Composable
private fun GeometricPatternLayer(palette: ThemePalette) {
    val infiniteTransition = rememberInfiniteTransition(label = "scaffoldParallaxTransition")

    val driftX1State = infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "driftX1"
    )
    val driftY1State = infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(32000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "driftY1"
    )
    val rotation1State = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(180000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation1"
    )

    val driftX2State = infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "driftX2"
    )
    val driftY2State = infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "driftY2"
    )
    val rotation2State = infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(120000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawGeometricPattern(
                    palette,
                    driftX1State.value,
                    driftY1State.value,
                    rotation1State.value,
                    driftX2State.value,
                    driftY2State.value,
                    rotation2State.value
                )
            }
    )
}

// ─────────────────────────────────────────────────
// SALAM TOP BAR — Consistent back navigation
// ─────────────────────────────────────────────────
@Composable
fun SalamTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val palette = LocalThemePalette.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(SalamSpacing.touchTarget)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.textPrimary,
                    modifier = Modifier.size(SalamSpacing.iconSize)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = palette.textPrimary,
                    fontWeight = FontWeight.ExtraBold
                ),
                maxLines = 1
            )
        }
        Row(content = actions)
    }
}

// ─────────────────────────────────────────────────
// SALAM SECTION HEADER — Section titles inside screens
// ─────────────────────────────────────────────────
@Composable
fun SalamSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val palette = LocalThemePalette.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = SalamSpacing.cardGap),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = palette.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            // Accent underline
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(4.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(palette.primary, palette.secondary, palette.accent.copy(alpha = 0f))
                        ),
                        shape = SalamShapes.pill
                    )
            )
        }
        trailingContent?.invoke()
    }
}

// ─────────────────────────────────────────────────
// SALAM ICON BADGE — Decorated icon circle
// ─────────────────────────────────────────────────
@Composable
fun SalamIconBadge(
    modifier: Modifier = Modifier,
    size: Dp = SalamSpacing.iconBackgroundSize,
    content: @Composable BoxScope.() -> Unit
) {
    val palette = LocalThemePalette.current

    Box(
        modifier = modifier
            .size(size)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        palette.primary.copy(alpha = 0.18f),
                        palette.secondary.copy(alpha = 0.1f),
                        palette.accent.copy(alpha = 0.08f)
                    )
                ),
                shape = SalamShapes.expressiveCorner2
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}

// ─────────────────────────────────────────────────
// GEOMETRIC PATTERN DRAWER — Subtle background art
// ─────────────────────────────────────────────────
private fun DrawScope.drawGeometricPattern(
    palette: ThemePalette,
    dx1: Float, dy1: Float, rot1: Float,
    dx2: Float, dy2: Float, rot2: Float
) {
    val patternColor = palette.primary.copy(alpha = 0.035f)
    val patternSize = 130f

    // --- LAYER 1: Slower & Larger (Deep Background) ---
    withTransform({
        translate(dx1, dy1)
        rotate(rot1, pivot = Offset(size.width * 0.8f, size.height * 0.12f))
    }) {
        drawEightPointStar(size.width * 0.8f, size.height * 0.12f, patternSize, patternColor)
    }

    withTransform({
        translate(dx1 * 0.6f, dy1 * 0.6f)
        rotate(rot1 * 0.8f, pivot = Offset(size.width * 0.15f, size.height * 0.88f))
    }) {
        drawEightPointStar(size.width * 0.15f, size.height * 0.88f, patternSize * 0.7f, patternColor.copy(alpha = 0.02f))
    }

    // --- LAYER 2: Faster & Smaller (Foreground Parallax Layer) ---
    withTransform({
        translate(dx2, dy2)
        rotate(rot2, pivot = Offset(size.width * 0.82f, size.height * 0.15f))
    }) {
        drawEightPointStar(size.width * 0.82f, size.height * 0.15f, patternSize * 0.75f, patternColor.copy(alpha = 0.02f))
    }

    withTransform({
        translate(dx2 * 0.8f, dy2 * 0.8f)
        rotate(rot2 * 1.2f, pivot = Offset(size.width * 0.2f, size.height * 0.84f))
    }) {
        drawEightPointStar(size.width * 0.2f, size.height * 0.84f, patternSize * 0.5f, patternColor.copy(alpha = 0.015f))
    }
}

private fun DrawScope.drawEightPointStar(
    cx: Float,
    cy: Float,
    radius: Float,
    color: Color
) {
    val path = Path()
    val points = 8
    val innerRadius = radius * 0.45f

    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = (i * PI / points - PI / 2).toFloat()
        val x = cx + r * cos(angle)
        val y = cy + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()

    drawPath(path, color, style = Stroke(width = 1.5f))

    // Inner circle
    drawCircle(
        color = color,
        radius = innerRadius * 0.5f,
        center = Offset(cx, cy),
        style = Stroke(width = 1f)
    )
}

// ─────────────────────────────────────────────────
// EXPRESSIVE ANIMATIONS
// ─────────────────────────────────────────────────



fun Modifier.breathingGlow() = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "breathingGlowTransition")
    val scaleState = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingGlowScale"
    )
    this.graphicsLayer {
        scaleX = scaleState.value
        scaleY = scaleState.value
    }
}

fun Modifier.shimmerLoading() = composed {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslation"
    )
    val palette = LocalThemePalette.current
    val shimmerColors = listOf(
        palette.surfaceVariant.copy(alpha = 0.6f),
        palette.shimmer,
        palette.surfaceVariant.copy(alpha = 0.6f)
    )
    this.drawBehind {
        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 300f, translateAnim - 300f),
            end = Offset(translateAnim, translateAnim)
        )
        drawRect(brush = brush)
    }
}

fun Modifier.elasticScale(targetScale: Float) = composed {
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "elasticScale"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

fun Modifier.slideInFromBottom() = composed {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(350),
        label = "slideInAlpha"
    )
    val translationY by animateFloatAsState(
        targetValue = if (visible) 0f else 80f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "slideInTranslationY"
    )
    this.graphicsLayer {
        this.alpha = alpha
        this.translationY = translationY
    }
}
