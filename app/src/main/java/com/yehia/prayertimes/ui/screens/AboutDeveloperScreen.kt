package com.yehia.prayertimes.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.ui.theme.*
import com.yehia.prayertimes.utils.LanguageManager

@Composable
fun AboutDeveloperScreen(
    onNavigateBack: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val context = LocalContext.current

    SalamScreenScaffold {
        SalamTopBar(
            title = "About the Developer",
            onNavigateBack = onNavigateBack
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Hero section: Developer Profile Card
            item {
                Box(modifier = Modifier.staggeredEntrance(1)) {
                    SalamCard(
                        modifier = Modifier.fillMaxWidth(),
                        isActive = true,
                        shape = SalamShapes.expressiveCorner1
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SalamSpacing.cardPaddingInner),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SalamIconBadge(
                                modifier = Modifier.size(72.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Developer Profile",
                                    tint = palette.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Yehia",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = palette.textPrimary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            
                            Text(
                                text = "Creator & Lead Developer of Salam",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = palette.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "A passionate software engineer focused on building elegant, performant, and privacy-centric Islamic utilities. Under Yehia's leadership, Salam was developed as an ad-free, open-source application to assist Muslims worldwide with accurate prayer timings, Qibla detection, Quran recitation, and daily Adhkar tracking.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = palette.textSecondary,
                                    lineHeight = 22.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // GitHub & Links Card
            item {
                Box(modifier = Modifier.staggeredEntrance(2)) {
                    SalamCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = SalamShapes.expressiveCorner2
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SalamSpacing.cardPaddingInner),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Developer Links",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = palette.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            // GitHub Link
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/YehiaHS"))
                                        context.startActivity(intent)
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "GitHub",
                                    tint = palette.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "GitHub Profile",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = palette.textPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "github.com/YehiaHS",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = palette.textMuted
                                        )
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Launch,
                                    contentDescription = "Open Link",
                                    tint = palette.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Divider(color = palette.outline.copy(alpha = 0.15f))

                            // Project Repository Link
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/YehiaHS/salamapp"))
                                        context.startActivity(intent)
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = "Source Code",
                                    tint = palette.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Salam Repository",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = palette.textPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Open Source Code & Issues Tracker",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = palette.textMuted
                                        )
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Launch,
                                    contentDescription = "Open Link",
                                    tint = palette.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Project Philosophy Card
            item {
                Box(modifier = Modifier.staggeredEntrance(3)) {
                    SalamCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = SalamShapes.expressiveCorner1
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SalamSpacing.cardPaddingInner)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Open Source Contribution",
                                    tint = palette.accent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "100% Free & Open Source",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = palette.textPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Salam is developed as Sadaqah Jariyah (voluntary ongoing charity). It will always remain free of ads, tracking, or paywalls. You can contribute to the translation, report bugs, or request features on our GitHub repository.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = palette.textSecondary,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
