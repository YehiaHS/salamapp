package com.yehia.prayertimes.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.R
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.theme.SalamShapes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isLoading: Boolean,
    onFinished: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    
    // Track if the entry animation minimum time has elapsed
    var minTimeElapsed by remember { mutableStateOf(false) }
    
    // Entry scale & alpha animation for the logo
    var startAnims by remember { mutableStateOf(false) }
    
    val logoScale by animateFloatAsState(
        targetValue = if (startAnims) 1f else 0.65f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnims) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing),
        label = "logoAlpha"
    )

    // Entry translate & alpha for text
    val textTranslationY by animateFloatAsState(
        targetValue = if (startAnims) 0f else 35f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "textTranslation"
    )

    // Breathing pulse for status loading text
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val statusAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "statusAlpha"
    )

    LaunchedEffect(Unit) {
        // Trigger animations on frame attachment
        startAnims = true
        // Guarantee the splash screen displays for at least 1200ms
        delay(1200L)
        minTimeElapsed = true
    }

    // Dismiss splash once minimum time is met and heavy datasets are fully parsed
    LaunchedEffect(isLoading, minTimeElapsed) {
        if (!isLoading && minTimeElapsed) {
            onFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        palette.background,
                        palette.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Emblem Brand Logo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Salam Logo",
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                        alpha = logoAlpha
                    }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // App Name "Salam"
            Text(
                text = "Salam",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    color = palette.textPrimary,
                    letterSpacing = 1.5.sp
                ),
                modifier = Modifier.graphicsLayer {
                    translationY = textTranslationY
                    alpha = logoAlpha
                }
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            // Smooth linear loading indicator
            LinearProgressIndicator(
                color = palette.primary,
                trackColor = palette.primary.copy(alpha = 0.15f),
                modifier = Modifier
                    .width(160.dp)
                    .height(4.dp)
                    .graphicsLayer {
                        alpha = logoAlpha
                    },
                strokeCap = StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Status message
            Text(
                text = "Preloading Sacred Text...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = palette.textSecondary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.graphicsLayer {
                    alpha = logoAlpha * statusAlpha
                }
            )
        }
    }
}
