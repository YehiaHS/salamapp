package com.yehia.prayertimes.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.ui.theme.*
import com.yehia.prayertimes.ui.viewmodel.PrayerViewModel
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MosqueHalalScreen(
    viewModel: PrayerViewModel,
    onNavigateBack: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val lat by viewModel.latitude.collectAsState()
    val lng by viewModel.longitude.collectAsState()

    // Filter categories: 0: Mosques, 1: Halal Food
    var activeCategory by remember { mutableStateOf(0) }

    // Generate local mock coordinates centered near user lat/lng
    val places = remember(lat, lng) {
        listOf(
            MockPlace("Masjid Al-Taqwa", "14 Roxy St, Heliopolis", 0.35, true, lat + 0.002, lng - 0.001),
            MockPlace("Masjid Al-Azhar Al-Sharif", "Al-Azhar District, Old Cairo", 1.80, true, lat + 0.015, lng + 0.008),
            MockPlace("Heliopolis Islamic Masjid", "24 Beirut St, Heliopolis", 0.72, true, lat - 0.004, lng + 0.003),
            MockPlace("El-Nour Masjid", "Abbassia Square, Cairo", 2.10, true, lat - 0.012, lng - 0.015),
            MockPlace("Roxy Halal Shawarma", "5 Roxy Square, Heliopolis", 0.22, false, lat + 0.001, lng + 0.001),
            MockPlace("Medina Grill & Kabab", "18 Khalifa St, Heliopolis", 0.58, false, lat + 0.003, lng - 0.002),
            MockPlace("Heliopolis Halal Burger", "Al-Mirghani St, Heliopolis", 0.95, false, lat - 0.005, lng + 0.005),
            MockPlace("Cairo Halal Koshary", "Al-Galaa St, Heliopolis", 1.40, false, lat + 0.008, lng - 0.010)
        )
    }

    val filteredPlaces = remember(activeCategory) {
        places.filter { if (activeCategory == 0) it.isMosque else !it.isMosque }
    }

    SalamScreenScaffold {
        SalamTopBar(
            title = "Mosque & Halal Finder",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
        ) {
            Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

            // Radar Scanner component isolated to avoid high-frequency root recompositions
            RadarScanner(
                lat = lat,
                lng = lng,
                filteredPlaces = filteredPlaces,
                palette = palette
            )

            // Sub Category Switch Row using custom styled cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
            ) {
                listOf("Nearby Mosques", "Halal Restaurants").forEachIndexed { index, name ->
                    val isSel = activeCategory == index
                    val tabShape = if (index == 0) SalamShapes.expressiveCorner1 else SalamShapes.expressiveCorner2
                    SalamCard(
                        modifier = Modifier.weight(1f),
                        isActive = isSel,
                        shape = tabShape,
                        onClick = { activeCategory = index }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) palette.primary else palette.textPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Places List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
            ) {
                itemsIndexed(filteredPlaces, key = { _, it -> it.name }) { index, place ->
                    val itemShape = if (index % 2 == 0) SalamShapes.expressiveCorner1 else SalamShapes.expressiveCorner2
                    SalamCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2,
                        shape = itemShape
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SalamSpacing.cardPaddingInner),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                SalamIconBadge {
                                    Icon(
                                        imageVector = if (place.isMosque) Icons.Default.Mosque else Icons.Default.Restaurant,
                                        contentDescription = null,
                                        tint = palette.primary,
                                        modifier = Modifier.size(SalamSpacing.iconSize)
                                    )
                                }
                                Spacer(modifier = Modifier.width(SalamSpacing.cardPaddingInner))
                                Column {
                                    Text(
                                        text = place.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textPrimary
                                        )
                                    )
                                    Text(
                                        text = place.address,
                                        style = MaterialTheme.typography.bodySmall.copy(color = palette.textSecondary)
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "%.2f km".format(place.distance),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = palette.primary
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(place.address))
                                        Toast.makeText(context, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(SalamSpacing.touchTarget)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Address",
                                        tint = palette.textSecondary,
                                        modifier = Modifier.size(14.dp)
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

@Composable
fun RadarScanner(
    lat: Double,
    lng: Double,
    filteredPlaces: List<MockPlace>,
    palette: ThemePalette
) {
    // Radar Scanning Animation is local to this composable
    val infiniteTransition = rememberInfiniteTransition(label = "radarTransition")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRotation"
    )

    SalamCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = 2,
        shape = SalamShapes.cardLarge
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(160.dp)) {
                val radius = size.minDimension / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Draw concentric radar grids
                drawCircle(color = palette.primary.copy(alpha = 0.1f), radius = radius, center = center, style = Stroke(1.dp.toPx()))
                drawCircle(color = palette.primary.copy(alpha = 0.15f), radius = radius * 0.66f, center = center, style = Stroke(1.dp.toPx()))
                drawCircle(color = palette.primary.copy(alpha = 0.2f), radius = radius * 0.33f, center = center, style = Stroke(1.dp.toPx()))

                // Draw cross axes lines
                drawLine(color = palette.primary.copy(alpha = 0.15f), start = Offset(0f, center.y), end = Offset(size.width, center.y), strokeWidth = 1.dp.toPx())
                drawLine(color = palette.primary.copy(alpha = 0.15f), start = Offset(center.x, 0f), end = Offset(center.x, size.height), strokeWidth = 1.dp.toPx())

                // Draw rotating scanning beam
                val angleRad = Math.toRadians(rotationAngle.toDouble())
                val endX = center.x + radius * cos(angleRad).toFloat()
                val endY = center.y + radius * sin(angleRad).toFloat()

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(palette.primary, Color.Transparent),
                        start = center,
                        end = Offset(endX, endY)
                    ),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 3.dp.toPx()
                )

                // Draw center pulse
                drawCircle(color = palette.primary, radius = 5.dp.toPx(), center = center)

                // Draw target points on the radar
                filteredPlaces.forEach { place ->
                    val dx = ((place.lat - lat) * 8000.0).toFloat()
                    val dy = ((place.lng - lng) * 8000.0).toFloat()
                    val targetCenter = Offset(
                        (center.x + dx).coerceIn(4f, size.width - 4f),
                        (center.y + dy).coerceIn(4f, size.height - 4f)
                    )

                    // Draw a glowing point for mosques or dining
                    drawCircle(
                        color = if (place.isMosque) palette.primary else palette.accent,
                        radius = 4.dp.toPx(),
                        center = targetCenter
                    )
                    drawCircle(
                        color = (if (place.isMosque) palette.primary else palette.accent).copy(alpha = 0.3f),
                        radius = 8.dp.toPx(),
                        center = targetCenter,
                        style = Stroke(1.dp.toPx())
                    )
                }
            }
        }
    }
}

data class MockPlace(
    val name: String,
    val address: String,
    val distance: Double,
    val isMosque: Boolean,
    val lat: Double,
    val lng: Double
)
