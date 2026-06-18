package com.yehia.prayertimes.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.data.Dua
import com.yehia.prayertimes.data.DuaCategory
import com.yehia.prayertimes.data.DuasData
import com.yehia.prayertimes.ui.theme.SalamCard
import com.yehia.prayertimes.ui.theme.SalamIconBadge
import com.yehia.prayertimes.ui.theme.SalamScreenScaffold
import com.yehia.prayertimes.ui.theme.SalamSectionHeader
import com.yehia.prayertimes.ui.theme.SalamSpacing
import com.yehia.prayertimes.ui.theme.SalamShapes
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.theme.staggeredEntrance
import com.yehia.prayertimes.ui.theme.salamClickable

@Composable
fun DuasScreen() {
    val palette by ThemeManager.currentPalette.collectAsState()
    val categories = DuasData.categories

    var expandedIndex by remember { mutableStateOf(-1) }

    SalamScreenScaffold {
        // Screen title
        Text(
            text = "Supplications & Adhkar",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = palette.textPrimary,
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier.padding(bottom = SalamSpacing.cardGap)
        )

        Text(
            text = "${categories.size} categories",
            style = MaterialTheme.typography.labelMedium.copy(
                color = palette.textMuted
            ),
            modifier = Modifier.padding(bottom = SalamSpacing.sectionGap)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(categories, key = { _, category -> category.name }) { index, category ->
                    val isExpanded = expandedIndex == index
                    CategoryDuaRow(
                        category = category,
                        isExpanded = isExpanded,
                        index = index,
                        palette = palette,
                        onToggle = {
                            expandedIndex = if (isExpanded) -1 else index
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(110.dp))
                }
            }

            // Top fading edge overlay to prevent abrupt card cutoff under header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(palette.background, Color.Transparent)
                        )
                    )
                    .align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun CategoryDuaRow(
    category: DuaCategory,
    isExpanded: Boolean,
    index: Int,
    palette: com.yehia.prayertimes.ui.theme.ThemePalette,
    onToggle: () -> Unit
) {

    val categoryIcon: ImageVector = when (category.icon) {
        "WbSunny" -> Icons.Default.WbSunny
        "NightsStay" -> Icons.Default.NightsStay
        "Bedtime" -> Icons.Default.Bedtime
        "Mosque" -> Icons.Default.Mosque
        "Restaurant" -> Icons.Default.Restaurant
        "Flight" -> Icons.Default.Flight
        "Home" -> Icons.Default.Home
        "Healing" -> Icons.Default.Healing
        else -> Icons.Default.AutoAwesome
    }

    val shape = if (index % 2 == 0) SalamShapes.expressiveCorner1 else SalamShapes.expressiveCorner2

    SalamCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = if (isExpanded) 3 else 2,
        isActive = isExpanded,
        shape = shape,
        onClick = onToggle
    ) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SalamSpacing.cardPaddingInner),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SalamIconBadge {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = palette.primary,
                    modifier = Modifier.size(SalamSpacing.iconSize)
                )
            }
            Spacer(modifier = Modifier.width(SalamSpacing.cardPaddingInner))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name.substringBefore(" ·"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = palette.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = category.name.substringAfter(" · ").trim(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = palette.primary.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = palette.textSecondary,
                modifier = Modifier.size(SalamSpacing.iconSize)
            )
        }

        // Expanded content with animation
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = SalamSpacing.cardPaddingInner,
                        end = SalamSpacing.cardPaddingInner,
                        bottom = SalamSpacing.cardPaddingInner
                    ),
                verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardPaddingInner)
            ) {
                category.duas.forEachIndexed { i, dua ->
                    DuaItem(dua = dua, index = i + 1, palette = palette)
                    if (i < category.duas.size - 1) {
                        HorizontalDivider(
                            color = palette.outline.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DuaItem(dua: Dua, index: Int, palette: com.yehia.prayertimes.ui.theme.ThemePalette) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Index badge
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(palette.primary.copy(alpha = 0.12f), SalamShapes.expressiveCorner1),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = index.toString(),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = palette.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

        // Arabic text right aligned
        Text(
            text = dua.arabic,
            style = MaterialTheme.typography.titleLarge.copy(
                color = palette.textPrimary,
                fontWeight = FontWeight.Bold,
                textDirection = TextDirection.Rtl,
                lineHeight = 34.sp
            ),
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

        // Transliteration
        Text(
            text = dua.transliteration,
            style = MaterialTheme.typography.bodySmall.copy(
                color = palette.primary,
                fontStyle = FontStyle.Italic,
                lineHeight = 18.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

        // Translation
        Text(
            text = dua.translation,
            style = MaterialTheme.typography.bodySmall.copy(
                color = palette.textSecondary,
                lineHeight = 18.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

        // Reference
        Text(
            text = "[${dua.reference}]",
            style = MaterialTheme.typography.labelSmall.copy(
                color = palette.textMuted,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
}
