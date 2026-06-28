package com.yehia.prayertimes.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.yehia.prayertimes.ui.theme.*
import com.yehia.prayertimes.ui.viewmodel.PrayerViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

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

    // Configure user agent for OpenStreetMap requirements
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

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

    val filteredPlaces = remember(activeCategory, places) {
        places.filter { if (activeCategory == 0) it.isMosque else !it.isMosque }
    }

    // Keep track of the selected place to pan/focus on OpenStreetMap
    var selectedPlace by remember(filteredPlaces) { mutableStateOf<MockPlace?>(null) }

    // Setup MapView state
    var osmMapViewInstance by remember { mutableStateOf<org.osmdroid.views.MapView?>(null) }

    // Update map markers when location coordinates or activeCategory/places change
    LaunchedEffect(filteredPlaces, osmMapViewInstance) {
        val map = osmMapViewInstance ?: return@LaunchedEffect
        map.overlays.clear()

        // 1. User current location marker
        val userGeoPoint = GeoPoint(lat, lng)
        val userMarker = Marker(map).apply {
            position = userGeoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Your Location"
        }
        map.overlays.add(userMarker)

        // 2. Add category markers
        filteredPlaces.forEach { place ->
            val placeGeoPoint = GeoPoint(place.lat, place.lng)
            val marker = Marker(map).apply {
                position = placeGeoPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = place.name
                subDescription = place.address
            }
            map.overlays.add(marker)
        }

        // Center on user location
        map.controller.setZoom(14.5)
        map.controller.setCenter(userGeoPoint)
        map.invalidate()
    }

    // Pan camera to specific place when item clicked
    LaunchedEffect(selectedPlace) {
        val map = osmMapViewInstance ?: return@LaunchedEffect
        val place = selectedPlace ?: return@LaunchedEffect
        map.controller.animateTo(GeoPoint(place.lat, place.lng))
        map.controller.setZoom(16.0)
    }

    SalamScreenScaffold {
        SalamTopBar(
            title = "Mosque & Halal Finder",
            onNavigateBack = onNavigateBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
        ) {
            // OpenStreetMap Card container
            item {
                Spacer(modifier = Modifier.height(SalamSpacing.elementGap))
                SalamCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    elevation = 3,
                    shape = SalamShapes.cardLarge
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            factory = { context ->
                                org.osmdroid.views.MapView(context).apply {
                                    setTileSource(TileSourceFactory.MAPNIK)
                                    setMultiTouchControls(true)
                                    zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
                                    osmMapViewInstance = this
                                }
                            },
                            update = { /* No-op */ },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Sub Category Switch Row using custom styled cards
            item {
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
            }

            // Places List with Click Pan + Maps Launcher Actions
            itemsIndexed(filteredPlaces, key = { _, it -> it.name }) { index, place ->
                val isSelected = selectedPlace?.name == place.name
                val itemShape = if (index % 2 == 0) SalamShapes.expressiveCorner1 else SalamShapes.expressiveCorner2
                
                SalamCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = if (isSelected) palette.primary else Color.Transparent,
                            shape = itemShape
                        ),
                    elevation = 2,
                    shape = itemShape,
                    onClick = { selectedPlace = place }
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Maps App Route Directions Intent Launcher
                                IconButton(
                                    onClick = {
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${place.lat},${place.lng}"))
                                        context.startActivity(webIntent)
                                    },
                                    modifier = Modifier.size(SalamSpacing.touchTarget)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Directions,
                                        contentDescription = "Get Directions",
                                        tint = palette.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(4.dp))

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

            item {
                Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
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
