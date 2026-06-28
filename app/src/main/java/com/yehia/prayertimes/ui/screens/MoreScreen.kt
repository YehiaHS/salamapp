package com.yehia.prayertimes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.yehia.prayertimes.ui.theme.SalamCard
import com.yehia.prayertimes.ui.theme.SalamIconBadge
import com.yehia.prayertimes.ui.theme.SalamScreenScaffold
import com.yehia.prayertimes.ui.theme.SalamSpacing
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.utils.LanguageManager
import com.yehia.prayertimes.ui.theme.staggeredEntrance
import com.yehia.prayertimes.ui.theme.salamClickable

@Composable
fun MoreScreen(
    onNavigateToQibla: () -> Unit,
    onNavigateToHadith: () -> Unit,
    onNavigateToHijri: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTracker: () -> Unit,
    onNavigateToNames: () -> Unit,
    onNavigateToHajj: () -> Unit,
    onNavigateToZakat: () -> Unit,
    onNavigateToAboutDeveloper: () -> Unit,
    onNavigateToLocator: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()

    // Read to trigger recomposition on language change
    LanguageManager.currentLang

    SalamScreenScaffold {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap),
            verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap),
            contentPadding = PaddingValues(bottom = 110.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = LanguageManager.get("tab_more"),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = palette.primary,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    modifier = Modifier.padding(bottom = SalamSpacing.cardPaddingInner)
                )
            }
            item {
                Box(modifier = Modifier.staggeredEntrance(0)) {
                    MoreMenuCard(
                        title = LanguageManager.get("title_qibla"),
                        subtitle = "Compass & Direction",
                        icon = Icons.Default.CompassCalibration,
                        shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner1,
                        onClick = onNavigateToQibla
                    )
                }
            }
            item {
                Box(modifier = Modifier.staggeredEntrance(1)) {
                    MoreMenuCard(
                        title = LanguageManager.get("title_hadith"),
                        subtitle = "Daily Islamic Quotes",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner2,
                        onClick = onNavigateToHadith
                    )
                }
            }
            item {
                Box(modifier = Modifier.staggeredEntrance(2)) {
                    MoreMenuCard(
                        title = LanguageManager.get("title_calendar"),
                        subtitle = "Hijri conversion & dates",
                        icon = Icons.Default.CalendarMonth,
                        shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner1,
                        onClick = onNavigateToHijri
                    )
                }
            }
            item {
                Box(modifier = Modifier.staggeredEntrance(3)) {
                    MoreMenuCard(
                        title = LanguageManager.get("title_tracker"),
                        subtitle = "Salah & Fasting tracker",
                        icon = Icons.Default.CheckCircle,
                        shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner2,
                        onClick = onNavigateToTracker
                    )
                }
            }
            item {
                Box(modifier = Modifier.staggeredEntrance(4)) {
                    MoreMenuCard(
                        title = LanguageManager.get("title_names"),
                        subtitle = "99 Names of Allah",
                        icon = Icons.Default.Star,
                        shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner1,
                        onClick = onNavigateToNames
                    )
                }
            }
            item {
                Box(modifier = Modifier.staggeredEntrance(5)) {
                    MoreMenuCard(
                        title = LanguageManager.get("title_hajj"),
                        subtitle = "Hajj & Umrah guides",
                        icon = Icons.Default.Mosque,
                        shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner2,
                        onClick = onNavigateToHajj
                    )
                }
            }
            item {
                Box(modifier = Modifier.staggeredEntrance(6)) {
                    MoreMenuCard(
                        title = LanguageManager.get("title_zakat"),
                        subtitle = "Assets calculator",
                        icon = Icons.Default.AccountBalance,
                        shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner1,
                        onClick = onNavigateToZakat
                    )
                }
            }
            item {
                Box(modifier = Modifier.staggeredEntrance(7)) {
                    MoreMenuCard(
                        title = LanguageManager.get("title_locator"),
                        subtitle = "Radar scan locations",
                        icon = Icons.Default.LocationOn,
                        shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner2,
                        onClick = onNavigateToLocator
                    )
                }
            }
            item {
                Box(modifier = Modifier.staggeredEntrance(8)) {
                    MoreMenuCard(
                        title = "About Developer",
                        subtitle = "Info about the developer",
                        icon = Icons.Default.Person,
                        shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner1,
                        onClick = onNavigateToAboutDeveloper
                    )
                }
            }
            item {
                Box(modifier = Modifier.staggeredEntrance(9)) {
                    MoreMenuCard(
                        title = LanguageManager.get("title_preferences"),
                        subtitle = "Calculation & Theme",
                        icon = Icons.Default.Settings,
                        shape = com.yehia.prayertimes.ui.theme.SalamShapes.expressiveCorner2,
                        onClick = onNavigateToSettings
                    )
                }
            }
        }
    }
}

@Composable
fun MoreMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    shape: androidx.compose.ui.graphics.Shape = com.yehia.prayertimes.ui.theme.SalamShapes.cardMedium,
    onClick: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()

    SalamCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        elevation = 2,
        shape = shape,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SalamSpacing.cardGap),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SalamIconBadge(
                size = 40.dp
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = palette.primary,
                    modifier = Modifier.size(SalamSpacing.iconSize)
                )
            }

            Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = palette.textPrimary,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = palette.textSecondary
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
